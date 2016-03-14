package com.foodenak.itpscanner.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by ITP on 5/8/2015.
 */
class LoadMoreDeterminer(var loadMoreCallback: Runnable?) : RecyclerView.OnScrollListener() {

    var isComplete = false

    var threshold = 20

    var isLoading = false

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val manager = recyclerView!!.layoutManager

        if (isComplete) {
            return
        }

        if (!isLoading) {
            val visibleItemCount = manager.childCount
            val totalItemCount = manager.itemCount
            val firstVisibleItems: Int
            if (manager is LinearLayoutManager) {
                firstVisibleItems = manager.findFirstVisibleItemPosition()
            } else {
                throw IllegalStateException("Unsupported LayoutManager, only support LinearLayoutManager or it's inheritance")
            }
            if ((visibleItemCount + firstVisibleItems) >= (totalItemCount - threshold)) {
                this.loadMoreCallback?.run()
            }
        }
    }
}
