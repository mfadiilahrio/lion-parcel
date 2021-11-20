package com.rio.commerce.feature.gallery.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.rio.commerce.commonui.BundleKey.DATA
import com.rio.commerce.commonui.view.AdapterListener
import com.rio.commerce.commonui.view.RecyclerViewAdapter
import com.rio.commerce.core.data.MediaInfo
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import com.rio.commerce.commonui.R as CommonR

class GalleryFragment : Fragment(), AdapterListener<MediaInfo> {

    @Inject
    lateinit var adapter: RecyclerViewAdapter<AdapterListener<MediaInfo>, MediaInfo, *>

    @Inject
    internal lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var listing: RecyclerView

    var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(CommonR.layout.fragment_list, container, false)

        val mediaList = arguments?.getParcelableArrayList<MediaInfo>(DATA)

        listing = view.findViewById(CommonR.id.listing)
        listing.layoutManager = layoutManager
        listing.adapter = adapter

        adapter.listener = this

        mediaList?.let {
            adapter.setList(it)
        }

        return view
    }

    override fun didTap(item: MediaInfo) {
        adapter.list.firstOrNull { it.filename == item.filename }?.let {
            listener?.itemDidTap(adapter.list.indexOf(it))
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    companion object {
        fun newInstance(
            mediaList: ArrayList<MediaInfo>?
        ): GalleryFragment {
            val fragment = GalleryFragment()

            val args = Bundle()
            args.putParcelableArrayList(DATA, mediaList)

            fragment.arguments = args

            return fragment
        }
    }

    interface Listener {
        fun itemDidTap(position: Int)
    }
}