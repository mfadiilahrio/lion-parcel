package com.rio.commerce.commonui.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.play.core.splitcompat.SplitCompat
import com.rio.commerce.commonui.BuildConfig
import com.rio.commerce.commonui.R
import com.rio.commerce.core.base.view.ListViewModel
import com.rio.commerce.core.base.view.ViewModelBinding
import com.rio.commerce.core.data.model.*

abstract class ListFragment<Request, Item> : Fragment(),
    EmptyView.Listener where Request : ListableRequest, Request : ForceReloadableRequest, Request : Parcelable {

    abstract var viewModel: ListViewModel<Request, Item, DataError>

    abstract var layoutManager: RecyclerView.LayoutManager

    var mRequest: Request? = null

    private val mBinding = ViewModelBinding()

    private var mLoadDataAfterViewCreated = false
    private var mRecyclerViewState: Parcelable? = null
    private var mIsRefreshing = false
    private var mTotalPages = 1
    private var mLoaded = false
    private var mDebugSkeleton = false
    private var mLoadMore = true

    private var mListing: RecyclerView? = null

    private var mEmptyView: EmptyView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    protected var skeleton: Skeleton? = null

    var title: String? = null
        set(value) {
            field = value

            if (isAdded && context != null) {
                mEmptyView?.title = value
            }
        }

    var subtitle: String? = null
        set(value) {
            field = value

            if (isAdded && context != null) {
                mEmptyView?.subtitle = value
            }
        }

    var actionTitle: String? = null
        set(value) {
            field = value

            if (isAdded && context != null) {
                mEmptyView?.actionTitle = value
            }
        }

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
        mEmptyView = view.findViewWithTag("emptyView")

        mEmptyView?.listener = this

        title?.let {
            mEmptyView?.title = title
            mEmptyView?.subtitle = subtitle
            mEmptyView?.actionTitle = actionTitle
        } ?: run {
            mEmptyView?.title = arguments?.getString(EMPTY_TITLE)
            mEmptyView?.subtitle = arguments?.getString(EMPTY_SUBTITLE)
            mEmptyView?.actionTitle = arguments?.getString(EMPTY_ACTION_TITLE)
        }

        mDebugSkeleton = arguments?.getBoolean(DEBUG_SKELETON) ?: false

        mListing?.layoutManager = layoutManager

        mLoadMore = arguments?.getBoolean(ALLOW_LOAD_MORE, true) ?: true
        mSwipeRefreshLayout?.isEnabled = arguments?.getBoolean(ALLOW_REFRESH) ?: false

        if (mLoadMore) {
            mListing?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val manager = layoutManager as LinearLayoutManager

                    val totalItemCount = manager.itemCount
                    val lastVisibleItem = manager.findLastVisibleItemPosition()
                    val page = mRequest?.page ?: 1

                    if (!mIsRefreshing && totalItemCount <= lastVisibleItem + LOAD_LIST_THRESHOLD && page < mTotalPages) {
                        loadMore()
                    }
                }
            })
        }

        mSwipeRefreshLayout?.setOnRefreshListener {
            reload(force = true, ignorePaging = false)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        SplitCompat.install(context)
    }

    override fun onDestroyView() {
        mBinding.dispose()

        super.onDestroyView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState?.getParcelable<Request>(REQUEST) != null) {
            mRequest = savedInstanceState.getParcelable(REQUEST)
        }

        mRecyclerViewState = savedInstanceState?.getParcelable(SCROLL_POSITION)
        mLoaded = savedInstanceState?.getBoolean(LOADED) ?: false
        mLoadDataAfterViewCreated = savedInstanceState?.getBoolean(LOAD_DATA_AFTER_CREATED) ?: true

        if (mLoadDataAfterViewCreated) { // Initial load

            mLoadDataAfterViewCreated = false

            mRequest?.let {
                loadData(it)
            }
        } else if (mRequest != null) { // View restored
            reload(force = !mLoaded, ignorePaging = mLoaded)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(SCROLL_POSITION, mListing?.layoutManager?.onSaveInstanceState())
        outState.putParcelable(REQUEST, mRequest)
        outState.putBoolean(LOADED, mLoaded)
        outState.putBoolean(LOAD_DATA_AFTER_CREATED, mLoadDataAfterViewCreated)
    }

    fun loadData(request: Request) {
        mRequest = request

        if (view == null) {
            mLoadDataAfterViewCreated = true
        } else {
            skeleton?.showSkeleton()

            viewModel.inputs.execute(request)
        }
    }

    fun toggleEmptyView(show: Boolean) {
        if (show) {
            mEmptyView?.visibility = View.VISIBLE
            mListing?.visibility = View.GONE
        } else {
            mEmptyView?.visibility = View.GONE
            mListing?.visibility = View.VISIBLE
        }
    }

    open fun unauthorized(string: String) {
        // Do Nothing
    }

    override fun actionDidTap() {
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
            viewModel.outputs.failed.observeOn(mainScheduler),
            onNext = ::failed
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

        if (data.request.page <= 1 && !data.isFromInitialCache && mEmptyView?.title != null) {
            toggleEmptyView(data.item.isEmpty())
        }

        val set = setList(data)

        if (set) {
            mRecyclerViewState?.let {
                mListing?.layoutManager?.onRestoreInstanceState(mRecyclerViewState)

                mRecyclerViewState = null
            }
        }

        mLoaded = true
    }

    private fun failed(error: DataError) {
        if (error.status == 105 && mEmptyView?.title != null) {
            toggleEmptyView(true)
        }
    }

    private fun exception(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d("EXCEPTION", throwable.stackTrace.joinToString("\n"))
        }
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

    fun reload(force: Boolean, ignorePaging: Boolean) {
        val request = mRequest ?: return

        if (force) {
            request.page = 1
        }

        request.timestamp = System.currentTimeMillis()
        request.forceReload = force
        request.ignorePaging = ignorePaging

        viewModel.inputs.execute(request)
    }

    private fun loadMore() {
        val request = mRequest ?: return

        request.page += 1
        request.forceReload = false
        request.ignorePaging = false

        viewModel.inputs.loadMore(request)
    }

    companion object {
        const val LOAD_LIST_THRESHOLD = 10
        const val LIMIT = 30

        const val ID = "bundle.id"
        const val LAYOUT = "bundle.layout"
        const val ALLOW_REFRESH = "bundle.allow_refresh"
        const val ALLOW_LOAD_MORE = "bundle.allow_load_more"
        const val EMPTY_TITLE = "bundle.empty_title"
        const val EMPTY_SUBTITLE = "bundle.empty_subtitle"
        const val EMPTY_ACTION_TITLE = "bundle.empty_action_title"
        const val REQUEST = "bundle.request"
        const val LOADED = "bundle.loaded"
        const val LOAD_DATA_AFTER_CREATED = "bundle.load_data_after_created"
        const val SCROLL_POSITION = "bundle.scroll_position"
        const val DEBUG_SKELETON = "bundle.debug_skeleton"
        const val SEARCH_AFTER = "bundle.search_after"
    }
}