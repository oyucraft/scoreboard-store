package net.kigawa.oyu.scoreboardstore.util.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object DefaultCoroutines {
  val defaultContext = Dispatchers.Default
  private val defaultScope
    get() = CoroutineScope(defaultContext)
  val ioContext = Dispatchers.IO
  private val ioScope
    get() = CoroutineScope(ioContext)
}