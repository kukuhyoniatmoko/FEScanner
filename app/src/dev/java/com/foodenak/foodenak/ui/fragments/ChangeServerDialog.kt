package com.foodenak.foodenak.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Process
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.foodenak.foodenak.rest.ServiceEndPoints
import com.foodenak.itpscanner.FEApplication
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.ui.AlertDialogFragment
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import kotlinx.android.synthetic.dev.change_service_end_point.*
import java.util.*

/**
 * Created by ITP on 3/4/2015.
 */
class ChangeServerDialog : DialogFragment(), AdapterView.OnItemClickListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.change_service_end_point, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    current_end_points.text = ServiceEndPoints.getDevEndPoint(context)
    server_end_points.setAdapter(Adapter(activity))
    server_end_points.onItemClickListener = this
    toolbar.title = "Change server domain"
    toolbar.navigationIcon = IconDrawable(activity, Iconify.IconValue.zmdi_close).colorRes(
        R.color.text_white_primary).actionBarSize()
    toolbar.setNavigationOnClickListener { v -> dismiss() }
    val item = toolbar.menu.add(R.string.save).setIcon(
        IconDrawable(activity, Iconify.IconValue.zmdi_check).colorRes(
            R.color.text_white_primary).actionBarSize())
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    item.setOnMenuItemClickListener({ item1 ->
      val endPoint = server_end_points.text.toString()
      if (TextUtils.isEmpty(endPoint)) return@setOnMenuItemClickListener true
      ServiceEndPoints.setDevEndPoint(activity, endPoint)
      showChangeServerAlert(endPoint)

      val component = (activity.applicationContext as FEApplication).component()
      val session = component.daoSession()
      session.userEntityDao.deleteAll()
      session.eventEntityDao.deleteAll()
      session.eventImageEntityDao.deleteAll()
      session.historyEntityDao.deleteAll()
      session.clear()
      UserSession.initialize(UserSession("", ""), context)
      dismiss()
      android.os.Process.killProcess(Process.myPid())
      true
    })
  }

  override fun onStart() {
    super.onStart()
    val d = dialog
    if (d != null) {
      val width = ViewGroup.LayoutParams.MATCH_PARENT
      val height = ViewGroup.LayoutParams.MATCH_PARENT
      d.window.setLayout(width, height)
    }
  }

  private fun showChangeServerAlert(endPoint: String) {
    val manager = childFragmentManager
    val tag = "change-server-alert"

    (manager.findFragmentByTag(tag) as AlertDialogFragment?)?.dismiss()

    AlertDialogFragment.newInstance("Change server to $endPoint").show(manager, tag)
  }

  override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
  }

  override fun onDestroyView() {
    super.onDestroyView()
  }

  companion object {
    private const val TAG = "ChangeServerDialog"
  }

  class Adapter(activity: Activity) : ArrayAdapter<String>(activity,
      R.layout.auto_complete_item, ArrayList()), Filterable {
    override fun getFilter(): Filter? {
      return object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults? {
          val results = FilterResults()
          if (TextUtils.isEmpty(p0)) {
            results.count = ServiceEndPoints.devEndPoints.size
            results.values = ServiceEndPoints.devEndPoints
          } else {
            val endPoints = ServiceEndPoints.devEndPoints.filter {
              it.contains(p0.toString(), true)
            }
            results.count = endPoints.size
            results.values = endPoints
          }
          return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
          clear()
          if (p1 == null || p1.count == 0) {
            notifyDataSetInvalidated()
          } else {
            @Suppress("UNCHECKED_CAST")
            addAll(p1.values as MutableList<String>)
            notifyDataSetChanged()
          }
        }
      }
    }
  }
}
