package com.foodenak.foodenak.ui.fragments

import android.support.v4.app.FragmentManager
import android.view.View

/**
 * Created by kukuh on 16/02/05.
 */
class ShowChangeServerLongClickListener(private val manager: FragmentManager) : View.OnLongClickListener {

  override fun onLongClick(v: View): Boolean {
    val dialog = ChangeServerDialog()
    dialog.show(manager, "change_server")
    return true
  }
}
