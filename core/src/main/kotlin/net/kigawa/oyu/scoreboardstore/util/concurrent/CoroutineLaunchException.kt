package net.kigawa.mcsm.util.concurrent

import kotlinx.coroutines.CoroutineScope

class CoroutineLaunchException(message: String?, coroutineContext: CoroutineScope, cause: Throwable?) :
  CoroutineException(message, coroutineContext, cause) {
}