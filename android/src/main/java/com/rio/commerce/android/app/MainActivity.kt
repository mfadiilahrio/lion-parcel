package com.rio.commerce.android.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import com.google.android.play.core.splitcompat.SplitCompat
import com.rio.commerce.android.app.fact.FactsFragment
import com.rio.commerce.commonui.BundleKey.DATA
import com.rio.commerce.commonui.base.SplitActivity
import com.rio.commerce.commonui.di.Modules
import com.rio.commerce.commonui.extension.appShared
import com.rio.commerce.core.data.model.ListRequest
import com.rio.commerce.core.fact.data.Fact
import dagger.android.AndroidInjection


class MainActivity : SplitActivity(), FactsFragment.Listener {

    private lateinit var mFragment: FactsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            mFragment =
                supportFragmentManager.findFragmentById(R.id.frame_container) as FactsFragment

        } else {
            mFragment = FactsFragment.newInstance(
                true,
                "",
                ""
            )

            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frame_container, mFragment)
                .commit()
        }

        mFragment.listener = this
        mFragment.loadData(ListRequest(forceReload = true))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        SplitCompat.install(this)
    }

    override fun didTap(fact: Fact) {
        loadModule(Modules.Transaction, R.id.coordinator_layout) {
            appShared.addModuleInjector(Modules.Transaction)

            Intent().setClassName(BuildConfig.APPLICATION_ID, Modules.View.FACT).apply {
                putExtra(DATA, fact)
            }.run {
                startActivity(this)
            }
        }
    }

}