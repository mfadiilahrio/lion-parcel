package com.rio.commerce.commonui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.rio.commerce.commonui.R
import com.rio.commerce.commonui.di.FeatureModule

abstract class SplitActivity : AppCompatActivity(), SplitInstallStateUpdatedListener {

    private var mOnSuccess: ((String) -> Unit)? = null

    private val mDisplayName = HashMap<String, String>()

    private var mLoadingLayout: ViewGroup? = null
    private var mLoadingText: TextView? = null

    private val mSplitInstallManager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    fun loadModule(
        module: FeatureModule,
        @IdRes root: Int,
        debugLoading: Boolean = false,
        func: (module: String) -> Unit
    ) {
        mOnSuccess = func

        if (debugLoading) {
            showLoading(getString(module.displayName), root)

            return
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.KITKAT) {

            mOnSuccess?.invoke(module.name)

            return
        } else if (mSplitInstallManager.installedModules.contains(module.name)) {

            mOnSuccess?.invoke(module.name)

            return
        }

        mDisplayName[module.name] = getString(module.displayName)

        installModule(module.name, getString(module.displayName), root)
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val name = state.moduleNames()[0]
                val displayName = mDisplayName[name]

                mLoadingText?.text = getString(R.string.downloading_x, displayName)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                val name = state.moduleNames()[0]
                val displayName = mDisplayName[name]

                mLoadingText?.text = getString(R.string.installing_x, displayName)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                val name = state.moduleNames()[0]

                SplitCompat.install(this)

                mDisplayName.remove(name)

                hideLoading {
                    mOnSuccess?.invoke(name)
                }
            }
            SplitInstallSessionStatus.FAILED -> {
                val name = state.moduleNames()[0]

                mDisplayName.remove(name)

                hideLoading {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.module_installation_failed)
                        .setPositiveButton(R.string.ok, null)
                        .show()
                }
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mSplitInstallManager.registerListener(this)
    }

    override fun onPause() {
        mSplitInstallManager.unregisterListener(this)

        super.onPause()
    }

    private fun installModule(name: String, displayName: String, @IdRes root: Int) {

        val request = SplitInstallRequest
            .newBuilder()
            .addModule(name)
            .build()

        mSplitInstallManager.startInstall(request)

        showLoading(displayName, root)
    }

    private fun hideLoading(complete: (() -> Unit)?) {
        val progressBar = mLoadingLayout?.findViewWithTag<ProgressBar>("loading")
        val textView = mLoadingLayout?.findViewWithTag<TextView>("loading_text")

        try {
            mLoadingLayout?.removeView(progressBar)
            mLoadingLayout?.removeView(textView)
        } catch (e: Exception) {
        }

        mLoadingLayout?.animate()?.alpha(0.0f)?.setDuration(100)
            ?.setInterpolator(AccelerateInterpolator())
            ?.withEndAction {
                val parent = mLoadingLayout?.parent as ViewGroup

                parent.removeView(mLoadingLayout)

                complete?.invoke()
            }?.start() ?: complete?.invoke()
    }

    private fun showLoading(name: String, @IdRes root: Int) {
        val view = LayoutInflater.from(this).run {
            val view: ViewGroup = findViewById(root)

            this.inflate(R.layout.view_loading, view, true)
        }

        mLoadingLayout = view.findViewById<ViewGroup>(R.id.loading_layout).apply {
            ViewCompat.setElevation(this, 100.0f)

            if (this.parent is ConstraintLayout) {
                val set = ConstraintSet()

                set.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

                set.applyTo(this.parent as ConstraintLayout)
            }

            setBackgroundColor(ContextCompat.getColor(this@SplitActivity, R.color.background))
            alpha = 0.0f
            animate()
                .alpha(1.0f)
                .setDuration(100)
                .setInterpolator(AccelerateInterpolator())
                .start()
        }

        mLoadingText = view.findViewById<TextView>(R.id.loading_text).apply {
            text = getString(R.string.preparing_x, name)
        }
    }
}