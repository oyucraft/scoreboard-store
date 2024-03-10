package net.kigawa.oyu.scoreboardstore.util.concurrent

import kotlinx.coroutines.CoroutineScope

class CoroutineLaunchException(message: String?, coroutineContext: CoroutineScope, cause: Throwable?) :
  CoroutineException(message, coroutineContext, cause) {
}