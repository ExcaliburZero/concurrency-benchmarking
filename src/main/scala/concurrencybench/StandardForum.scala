package concurrencybench

import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.ConcurrentHashMap

class StandardForum extends Forum {
  val threads = new ConcurrentHashMap[Int,String]()

  def readThread(threadId: Int): String = {
    threads.get(threadId)
  }

  def replyToThread(threadId: Int, msg: String): Unit = {
    threads.put(threadId, msg)
  }

  def createThread(): Unit = {
    val random = ThreadLocalRandom.current()

    var continue = true
    while (continue) {
      val threadId = random.nextInt()

      if (threads.putIfAbsent(threadId, "New Thread\n") != null) {
        continue = false
      }
    }
  }

  def getRandomThreadId(): Int = {
    val random = ThreadLocalRandom.current()

    val threadIds = java.util.Collections.list(threads.keys())
    val index = random.nextInt(threadIds.size)

    threadIds.get(index)
  }
}
