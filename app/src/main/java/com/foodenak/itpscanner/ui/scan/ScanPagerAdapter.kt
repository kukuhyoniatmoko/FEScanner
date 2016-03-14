package com.foodenak.itpscanner.ui.scan

import android.content.Context
import android.content.res.Configuration
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/7/2015.
 */
class ScanPagerAdapter(val context: Context, manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

    val INDEX_SCAN = 0

    val INDEX_HISTORY = 1

    val PAGE_COUNT = 2

    override fun getItem(position: Int): Fragment? {
        when (position) {
            INDEX_SCAN -> return ScanFragment()
            INDEX_HISTORY -> return HistoryFragment()
            else -> return null
        }
    }

    override fun getCount(): Int {
        return PAGE_COUNT;
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            INDEX_SCAN -> return context.getString(R.string.scan)
            INDEX_HISTORY -> return context.getString(R.string.history)
            else -> return null
        }

    }

    override fun getPageWidth(position: Int): Float {
        val configuration = context.resources.configuration

        val orientation = configuration.orientation

        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0.5.toFloat() else 1.0.toFloat()
    }
}