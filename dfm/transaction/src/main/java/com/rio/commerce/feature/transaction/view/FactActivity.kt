package com.rio.commerce.feature.transaction.view

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.splitcompat.SplitCompat
import com.rio.commerce.commonui.BundleKey.DATA
import com.rio.commerce.commonui.extension.contactSupport
import com.rio.commerce.commonui.extension.showBackButton
import com.rio.commerce.core.base.view.ViewModel
import com.rio.commerce.core.base.view.ViewModelBinding
import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.fact.data.Fact
import com.rio.commerce.feature.transaction.R
import dagger.android.AndroidInjection
import javax.inject.Inject
import com.rio.commerce.android.app.R as MainR
import com.rio.commerce.commonui.R as CommonR

class FactActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: ViewModel<Nothing, Fact, DataError>

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mTvFact: TextView
    private lateinit var mTvLength: TextView

    private var mFact: Fact? = null

    private val mBinding = ViewModelBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fact)

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout)
        mTvFact = findViewById(R.id.fact)
        mTvLength = findViewById(R.id.length)

        mFact = intent?.getParcelableExtra(DATA)

        mFact?.let {
            setView(it)
        }

        binding()

        mSwipeRefreshLayout.setOnRefreshListener {
            viewModel.inputs.execute(null)
        }

        showBackButton()
    }

    override fun onDestroy() {
        mBinding.dispose()

        super.onDestroy()
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

    private fun binding() {
        with(mBinding) {
            subscribe(viewModel.outputs.loading.observeOn(mainScheduler)) {
                mSwipeRefreshLayout.isRefreshing = it.start
            }
            mergeAndSubscribe(
                listOf(
                    viewModel.outputs.unauthorized.observeOn(mainScheduler),
                    viewModel.outputs.failed.observeOn(mainScheduler)
                ), onNext = ::failed
            )
            subscribe(viewModel.outputs.result.observeOn(mainScheduler), onNext = ::success)
            subscribe(
                viewModel.outputs.exception.observeOn(mainScheduler),
                onNext = ::exception
            )
        }
    }

    private fun success(response: DataModel<Nothing?, Fact>) {
        setView(response.item)
    }

    private fun setView(fact: Fact) {
        mTvFact.text = fact.fact
        mTvLength.text = fact.length.toString()
    }

    private fun exception(error: Throwable) {
        contactSupport(
            MainR.string.email_support,
            CommonR.string.error,
            CommonR.string.failed_load_data,
            error
        )
    }

    private fun failed(error: DataError) {
        MaterialAlertDialogBuilder(this)
            .setTitle(CommonR.string.information)
            .setMessage(error.message)
            .setPositiveButton(CommonR.string.ok, null)
            .show()
    }
}