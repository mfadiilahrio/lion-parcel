package com.rio.commerce.android.app.fact

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.applySkeleton
import com.rio.commerce.commonui.BundleKey.SKELETON_RADIUS
import com.rio.commerce.commonui.view.AdapterListener
import com.rio.commerce.commonui.view.ListFragment
import com.rio.commerce.commonui.view.RecyclerViewAdapter
import com.rio.commerce.core.base.view.ListViewModel
import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.ListRequest
import com.rio.commerce.core.fact.data.Fact
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import com.rio.commerce.commonui.R as CommonR

class FactsFragment : ListFragment<ListRequest, Fact>(),
    AdapterListener<Fact> {

    @Inject
    override lateinit var viewModel: ListViewModel<ListRequest, Fact, DataError>

    @Inject
    override lateinit var layoutManager: RecyclerView.LayoutManager

    @Inject
    lateinit var adapter: RecyclerViewAdapter<AdapterListener<Fact>, Fact, *>

    private lateinit var listing: RecyclerView

    var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        listing = view?.findViewById(CommonR.id.listing)
            ?: throw Throwable("Listing is null")

        listing.adapter = adapter

        adapter.listener = this

        skeleton =
            listing.applySkeleton(CommonR.layout.skeleton_list, 3, cornerRadius = SKELETON_RADIUS)

        return view
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun setList(response: DataModel<ListRequest, List<Fact>>): Boolean {
        adapter.setList(response.item)

        return true
    }

    override fun adapterLoadingState(isLoading: Boolean) {
        adapter.loading = isLoading
    }

    override fun didTap(item: Fact) {
        listener?.didTap(item)
    }

    companion object {
        fun newInstance(
            allowRefresh: Boolean,
            emptyTitle: String? = null,
            emptySubtitle: String? = null
        ): FactsFragment {
            val fragment = FactsFragment()

            val args = Bundle()

            args.putBoolean(ALLOW_REFRESH, allowRefresh)
            args.putString(EMPTY_TITLE, emptyTitle)
            args.putString(EMPTY_SUBTITLE, emptySubtitle)

            fragment.arguments = args

            return fragment
        }
    }

    interface Listener {
        fun didTap(fact: Fact)
    }

}