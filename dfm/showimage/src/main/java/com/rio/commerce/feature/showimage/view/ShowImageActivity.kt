package com.rio.commerce.feature.showimage.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.play.core.splitcompat.SplitCompat
import com.rio.commerce.commonui.BundleKey.DESCRIPTION_LIST
import com.rio.commerce.commonui.BundleKey.LIST
import com.rio.commerce.commonui.BundleKey.POSITION
import com.rio.commerce.commonui.extension.makeActionBarTransparent
import com.rio.commerce.feature.showimage.R
import com.veinhorn.scrollgalleryview.ScrollGalleryView
import com.veinhorn.scrollgalleryview.builder.GallerySettings
import ogbe.ozioma.com.glideimageloader.dsl.DSL

class ShowImageActivity : AppCompatActivity() {

    private lateinit var mScrollGalleryView: ScrollGalleryView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaList = intent.getStringArrayListExtra(LIST)
        val position = intent.getIntExtra(POSITION, 0)
        val descriptionList = intent.getStringArrayListExtra(DESCRIPTION_LIST)

        setContentView(R.layout.activity_show_image)

        mScrollGalleryView = findViewById(R.id.gallery_view)

        val builder = ScrollGalleryView.from(mScrollGalleryView)
            .settings(
                GallerySettings
                    .from(supportFragmentManager)
                    .thumbnailSize(100)
                    .enableZoom(true)
                    .build()
            )
            .onPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                }
            })

        mediaList?.let {
            descriptionList?.forEachIndexed { index, description ->
                builder.add(DSL.image(it[index], description))
            } ?: builder.add(it.map { DSL.image(it) })
        }

        makeActionBarTransparent()

        mScrollGalleryView = builder.build()
        mScrollGalleryView.withHiddenThumbnails(true)
        mScrollGalleryView.currentItem = position
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        SplitCompat.install(this)
    }
}
