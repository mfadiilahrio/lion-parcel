package com.rio.commerce.feature.gallery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import com.google.android.play.core.splitcompat.SplitCompat
import com.rio.commerce.android.app.BuildConfig
import com.rio.commerce.commonui.BundleKey
import com.rio.commerce.commonui.BundleKey.DATA
import com.rio.commerce.commonui.base.SplitActivity
import com.rio.commerce.commonui.di.Modules
import com.rio.commerce.commonui.extension.appShared
import com.rio.commerce.commonui.extension.showBackButton
import com.rio.commerce.core.data.MediaInfo
import dagger.android.AndroidInjection

class GalleryActivity : SplitActivity(), GalleryFragment.Listener {

    private lateinit var mFragment: GalleryFragment
    private var mMediaList: MutableList<MediaInfo> = mutableListOf()

    companion object {
        const val WRITE_REVIEW = 0x1
    }

    private var mInvoiceId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        val mediaList = intent?.getParcelableArrayListExtra<MediaInfo>(DATA)

        mediaList?.let {
            mMediaList.addAll(it)
        }

        if (savedInstanceState != null) {
            mFragment =
                supportFragmentManager.findFragmentById(android.R.id.content) as GalleryFragment

        } else {
            mFragment = GalleryFragment.newInstance(
                mediaList
            )

            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.content, mFragment)
                .commit()
        }

        mFragment.listener = this
        showBackButton()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        SplitCompat.install(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun itemDidTap(position: Int) {
        loadModule(
            Modules.ShowImage,
            android.R.id.content
        ) {
            appShared.addModuleInjector(Modules.ShowImage)

            Intent().setClassName(BuildConfig.APPLICATION_ID, Modules.View.SHOW_IMAGE).apply {
                putExtra(BundleKey.POSITION, position)
                putStringArrayListExtra(
                    BundleKey.LIST,
                    ArrayList(mMediaList.map { media -> media.media.original })
                )
            }.run {
                startActivity(this)
            }
        }
    }


}