package net.kigawa.oyu.scoreboardstore.util.concurrent

import kotlinx.coroutines.CoroutineScope

open class CoroutineException(message: String?, coroutineContext: CoroutineScope, cause: Throwable?) :
  RuntimeException("$message | context: $coroutineContext", cause) {
}