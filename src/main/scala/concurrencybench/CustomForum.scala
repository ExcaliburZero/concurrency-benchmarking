package concurrencybench

import java.util.concurrent.ThreadLocalRandom

import scala.collection.mutable.ArrayBuffer

class CustomForum extends Forum {
  val lock = new ReadWriteLock()
  val threads = new ArrayBuffer[ForumThread]()

  def readThread(threadId: Int): Option[String] = {
    lock.lockRead()
    val value = try {
      threads(threadId)
    } finally {
      lock.unlockRead()
    }

    value match {
      case Left(_) => None
      case Right(msg) => Some(msg)
    }
  }

  def replyToThread(threadId: Int, msg: String): Boolean = {
    lock.lockWrite()
    try {
      threads(threadId) match {
        case Right(prev) =>
          threads.update(threadId, Right(prev + msg))
          true
        case Left(_) =>
          false
      }
    } finally {
      lock.unlockWrite()
    }
  }

  def createThread(): Unit = {
    lock.lockWrite()
    try {
      threads += Right("")
    } finally {
      lock.unlockWrite()
    }
  }

  def getRandomThreadId(): Int = {
    val random = ThreadLocalRandom.current()

    lock.lockRead()
    val numThreads = try {
      threads.size
    } finally {
      lock.unlockRead()
    }

    random.nextInt(numThreads)
  }

  def closeThread(threadId: Int): Unit = {
    lock.lockWrite()
    try {
      val newMsg: ForumThread = threads(threadId) match {
        case Left(msg) => Left(msg)
        case Right(msg) => Left(msg)
      }
      threads.update(threadId, newMsg)
    } finally {
      lock.unlockWrite()
    }
  }
}
