package com.foodenak.itpscanner.ui.scan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.utils.obtainActivityComponent
import kotlinx.android.synthetic.main.fragment_scan_enabler.*
import javax.inject.Inject

/**
 * Created by ITP on 10/7/2015.
 */
class ScanEnablerFragment : Fragment() {

    @Inject lateinit var viewModel: ScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.obtainActivityComponent<Component>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_scan_enabler, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        enableScanButton.setOnClickListener(viewModel.startScanButtonClickListener)
    }
}