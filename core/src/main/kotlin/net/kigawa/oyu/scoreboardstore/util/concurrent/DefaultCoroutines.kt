package net.kigawa.mcsm.util.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object DefaultCoroutines {
  val defaultContext = Dispatchers.Default
  private val defaultScope
    get() = CoroutineScope(defaultContext)
  val ioContext = Dispatchers.IO
  private val ioScope
    get() = CoroutineScope(ioContext)
}