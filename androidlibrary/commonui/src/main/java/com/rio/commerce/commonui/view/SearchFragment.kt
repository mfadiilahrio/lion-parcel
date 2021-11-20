package com.rio.commerce.commonui.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.faltenreich.skeletonlayout.Skeleton
import com.rio.commerce.commonui.R
import com.rio.commerce.core.base.view.SearchViewModel
import com.rio.commerce.core.base.view.ViewModelBinding
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.ForceReloadableRequest
import com.rio.commerce.core.data.model.ListableRequest
import com.rio.commerce.core.data.model.Loading

abstract class SearchFragment<Request, Item> :
    Fragment() where Request : ListableRequest, Request : ForceReloadableRequest, Request : Parcelable {

    abstract var viewModel: SearchViewModel<Request, Item>

    abstract var layoutManager: RecyclerView.LayoutManager

    var mRequest: Request? = null

    private val mBinding = ViewModelBinding()

    private var mLoadDataAfterViewCreated = false
    private var mRecyclerViewState: Parcelable? = null
    private var mIsRefreshing = false
    private var mTotalPages = 1
    private var mLoaded = false
    private var mDebugSkeleton = false

    private var mListing: RecyclerView? = null

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mSearchAfter: Int = 2

    protected var skeleton: Skeleton? = null

    abstract fun setList(response: DataModel<Request, List<Item>>): Boolean
    abstract fun adapterLoadingState(isLoading: Boolean)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding()

        val layout = if (arguments == null || arguments?.getInt(LAYOUT) == 0) {
            R.layout.fragment_list
        } else {
            arguments?.getInt(LAYOUT)
        } ?: throw Throwable("Layout must not null")

        val view = inflater.inflate(layout, container, false)

        mListing = view.findViewWithTag("listing")
        mSwipeRefreshLayout = view.findViewWithTag("swipeRefresh")

        mDebugSkeleton = arguments?.getBoolean(DEBUG_SKELETON) ?: false

        mListing?.layoutManager = layoutManager

        mSwipeRefreshLayout?.isEnabled = arguments?.getBoolean(ALLOW_REFRESH) ?: false

        mSwipeRefreshLayout?.setOnRefreshListener {
            reload(force = true, ignorePaging = false)
        }

        return view
    }


    override fun onDestroyView() {
        mBinding.dispose()

        super.onDestroyView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState?.getParcelable<Request>(REQUEST) != null && savedInstanceState.get(
                SEARCH_AFTER
            ) != null
        ) {
            mRequest = savedInstanceState.getParcelable(REQUEST)
            mSearchAfter = savedInstanceState.getInt(SEARCH_AFTER)
        }

        mRecyclerViewState = savedInstanceState?.getParcelable(SCROLL_POSITION)
        mLoaded = savedInstanceState?.getBoolean(LOADED) ?: false
        mLoadDataAfterViewCreated = savedInstanceState?.getBoolean(LOAD_DATA_AFTER_CREATED) ?: true

        if (mLoadDataAfterViewCreated) { // Initial load

            mLoadDataAfterViewCreated = false

            mRequest?.let {
                loadData(it, mSearchAfter)
            }
        } else if (mRequest != null) { // View restored
            reload(force = !mLoaded, ignorePaging = mLoaded)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(SCROLL_POSITION, mListing?.layoutManager?.onSaveInstanceState())
        outState.putParcelable(REQUEST, mRequest)
        outState.putInt(SEARCH_AFTER, mSearchAfter)
        outState.putBoolean(LOADED, mLoaded)
        outState.putBoolean(LOAD_DATA_AFTER_CREATED, mLoadDataAfterViewCreated)
    }

    fun loadData(request: Request, searchAfter: Int) {
        mRequest = request
        mSearchAfter = searchAfter

        if (view == null) {
            mLoadDataAfterViewCreated = true
        } else {
            viewModel.inputs.loadSavedResults(request)

            if (mSearchAfter == 0) {
                skeleton?.showSkeleton()
                viewModel.inputs.search(request)
            }
        }
    }

    open fun unauthorized(string: String) {
        // Do Nothing
    }

    private fun binding() {
        mBinding.subscribe(viewModel.outputs.loading.observeOn(mainScheduler), onNext = ::loading)
        mBinding.subscribe(viewModel.outputs.result.observeOn(mainScheduler), onNext = ::result)
        mBinding.subscribe(
            viewModel.outputs.exception.observeOn(mainScheduler),
            onNext = ::exception
        )
        mBinding.subscribe(
            viewModel.outputs.unauthorized.observeOn(mainScheduler),
            onNext = ::unauthorized
        )
    }

    private fun result(data: DataModel<Request, List<Item>>) {

        if (!mDebugSkeleton && data.request.page <= 1) {
            skeleton?.showOriginal()
        }

        mTotalPages = data.metadata?.pagination?.totalPages ?: 1

        val set = setList(data)

        if (set) {
            mRecyclerViewState?.let {
                mListing?.layoutManager?.onRestoreInstanceState(mRecyclerViewState)

                mRecyclerViewState = null
            }
        }

        mLoaded = true
    }

    private fun exception(throwable: Throwable) {
        throw throwable
    }

    private fun loading(loading: Loading) {
        val page = mRequest?.page ?: 1

        mIsRefreshing = loading.start

        if (loading.start) {

            if (page > 1) {
                adapterLoadingState(loading.start)
            }

            if (!loading.hideRefreshControlDuringLoading) {
                if (mSwipeRefreshLayout?.isEnabled == true) {
                    mSwipeRefreshLayout?.isRefreshing = true
                }

                if (mDebugSkeleton) {
                    skeleton?.showSkeleton()
                }
            } else if (page <= 1) {
                skeleton?.showSkeleton()
            }
        } else {
            if (loading.hideRefreshControlAfterFinished) {
                if (mSwipeRefreshLayout?.isEnabled == true) {
                    mSwipeRefreshLayout?.isRefreshing = false
                }
            }

            if (page > 1) {
                adapterLoadingState(false)
            }
        }
    }

    private fun reload(force: Boolean, ignorePaging: Boolean) {
        val request = mRequest ?: return

        if (force) {
            request.page = 1
        }

        request.timestamp = System.currentTimeMillis()
        request.forceReload = force
        request.ignorePaging = ignorePaging

        viewModel.inputs.search(request)
    }

    companion object {
        const val LIMIT = 30
        const val ID = "bundle.id"
        const val LAYOUT = "bundle.layout"
        const val ALLOW_REFRESH = "bundle.allow_refresh"
        const val REQUEST = "bundle.request"
        const val LOADED = "bundle.loaded"
        const val LOAD_DATA_AFTER_CREATED = "bundle.load_data_after_created"
        const val SCROLL_POSITION = "bundle.scroll_position"
        const val DEBUG_SKELETON = "bundle.debug_skeleton"
        const val SEARCH_AFTER = "bundle.search_after"
    }

}