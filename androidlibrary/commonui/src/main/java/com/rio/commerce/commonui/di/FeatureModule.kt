package com.rio.commerce.commonui.di

import androidx.annotation.StringRes
import com.rio.commerce.commonui.R

interface FeatureModule {
    val name: String
    val displayName: Int
    val injectorName: String
}

object Modules {
    private const val PACKAGE = "com.rio.commerce.feature"
    private const val SHOW_IMAGE = "$PACKAGE.showimage.di.FeatureInjector"
    private const val GALLERY = "$PACKAGE.gallery.di.FeatureInjector"
    private const val TRANSACTION = "$PACKAGE.transaction.di.FeatureInjector"

    object ShowImage : FeatureModule {
        override val name = "feature_showimage"

        @StringRes
        override val displayName = R.string.title_show_image
        override val injectorName = SHOW_IMAGE
    }

    object Gallery : FeatureModule {
        override val name = "feature_gallery"

        @StringRes
        override val displayName = R.string.title_gallery
        override val injectorName = GALLERY
    }

    object Transaction : FeatureModule {
        override val name = "feature_transaction"

        @StringRes
        override val displayName = R.string.title_transaction
        override val injectorName = TRANSACTION
    }

    object View {
        const val SHOW_IMAGE = "$PACKAGE.showimage.view.ShowImageActivity"
        const val GALLERY = "$PACKAGE.gallery.view.GalleryActivity"
        const val FACT = "$PACKAGE.transaction.view.FactActivity"
    }
}