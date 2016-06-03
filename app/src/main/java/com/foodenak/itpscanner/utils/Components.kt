package com.foodenak.itpscanner.utils

import android.app.Activity
import android.content.Context
import com.foodenak.itpscanner.FEApplication
import com.foodenak.itpscanner.FEApplicationComponent

/**
 * Created by ITP on 10/5/2015.
 */

fun Context.obtainApplicationComponent(): FEApplicationComponent {
  return (this.applicationContext as FEApplication).component()
}

inline fun <reified T> Activity.obtainActivityComponent(): T {
  if (this is HasComponent<*>) {
    val component = component();
    if (component is T) {
      return component;
    } else {
      throw RuntimeException("Activity has component, but not the type that required");
    }
  } else {
    throw RuntimeException("Activity doesn't have component");
  }
}