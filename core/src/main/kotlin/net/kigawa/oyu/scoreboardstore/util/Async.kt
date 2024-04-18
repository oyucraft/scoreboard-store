package net.kigawa.oyu.scoreboardstore.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import net.kigawa.kutil.unitapi.annotation.Kunit
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.logging.Level

@Kunit
class Async(private val plugin: PluginBase) {
    private val channel = Channel<Runnable>(10)
    private val queues = mutableMapOf<Any, Queue<FutureTask<*>>>()

    init {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            runBlocking {
                val end = Calendar.getInstance().timeInMillis + MAX_TICK_TIME
                while (true) {
                    val task = channel.tryReceive().getOrNull() ?: break
                    task.run()
                    if (end > Calendar.getInstance().timeInMillis) break
                }
            }
        }, 0, 0)
    }

    fun runTaskAsyncTimer(sec: Int, runnable: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, sec * 20L)
    }

    fun execute(runnable: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        val future = FutureTask(callable)
        execute {
            future.run()
            try {
                future.get()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
        return future
    }

    suspend fun runTaskSync(runnable: Runnable) {
        channel.send(runnable)
    }

    fun <T> submitQueue(lock: Any, callable: Callable<T>): Future<T> {
        val future = FutureTask(callable)

        synchronized(queues) {
            var queue = queues[lock]
            if (queue == null) {
                queue = LinkedList()
                queue.add(future)
                queues[lock] = queue
                runQueue(lock, queue)
            } else queue.add(future)
        }

        return future
    }

    private fun runQueue(key: Any, queue: Queue<FutureTask<*>>) {
        execute {
            while (true) {
                val task = synchronized(queues) {
                    val futureTask = queue.poll()
                    if (futureTask == null) {
                        queues.remove(key)
                        return@execute
                    }
                    futureTask
                }
                task.run()
                try {
                    task.get()
                } catch (e: ExecutionException) {
                    plugin.logger.log(Level.WARNING, "there an exception on submitQueue", e)
                }
            }
        }
    }

    fun close() {
        channel.close()
        runBlocking {
            for (runnable in channel) {
                runnable.run()
            }
        }
    }

    companion object {
        const val MAX_TICK_TIME: Int = 25
    }
}