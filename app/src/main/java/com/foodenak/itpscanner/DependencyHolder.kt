package com.foodenak.itpscanner

import java.util.*

/**
 * Created by ITP on 10/10/2015.
 */
class DependencyHolder {

  val holder = HashMap<String, Any>()

  fun hold(key: String, obj: Any): Any? = holder.put(key, obj)

  fun getObj(key: String): Any? = holder[key]

  fun release(key: String): Any? = holder.remove(key)
}