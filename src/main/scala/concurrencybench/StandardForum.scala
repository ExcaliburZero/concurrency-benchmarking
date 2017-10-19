package concurrencybench

import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiFunction

class StandardForum extends Forum {
  val threads = new ConcurrentHashMap[Int,ForumThread]()

  def readThread(threadId: Int): Option[String] = {
    val value = threads.get(threadId)
    if (value == null) {
      None
    } else {
      value match {
        case Left(_) => None
        case Right(msg) => Some(msg)
      }
    }
  }

  def replyToThread(threadId: Int, msg: String): Boolean = {
    var changed = false
    val func: BiFunction[Int,ForumThread,ForumThread] = (key, value) => {
      value match {
        case Left(_) => value
        case Right(_) =>
          changed = true
          Right(msg)
      }
    }
    threads.compute(threadId, func)
    changed
  }

  def createThread(): Unit = {
    val random = ThreadLocalRandom.current()

    var continue = true
    while (continue) {
      val threadId = random.nextInt()

      if (threads.putIfAbsent(threadId, Right("New Thread\n")) != null) {
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

  def closeThread(threadId: Int): Unit = {
  }
}
