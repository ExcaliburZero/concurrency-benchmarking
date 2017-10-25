package concurrencybench

import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.ThreadLocalRandom

import scala.collection.mutable.ArrayBuffer

class CustomForum extends Forum {
  val lock = new ReentrantReadWriteLock()
  val threads = new ArrayBuffer[ForumThread]()

  def readThread(threadId: Int): Option[String] = {
    lock.readLock().lock()
    val value = try {
      threads(threadId)
    } finally {
      lock.readLock().unlock()
    }

    value match {
      case Left(_) => None
      case Right(msg) => Some(msg)
    }
  }

  def replyToThread(threadId: Int, msg: String): Boolean = {
    lock.writeLock().lock()
    try {
      threads(threadId) match {
        case Right(prev) =>
          threads.update(threadId, Right(prev + msg))
          true
        case Left(_) =>
          false
      }
    } finally {
      lock.writeLock().unlock()
    }
  }

  def createThread(): Unit = {
    lock.writeLock().lock()
    try {
      threads += Right("New Thread\n")
    } finally {
      lock.writeLock().unlock()
    }
  }

  def getRandomThreadId(): Int = {
    val random = ThreadLocalRandom.current()

    lock.readLock().lock()
    val numThreads = try {
      threads.size
    } finally {
      lock.readLock().unlock()
    }

    random.nextInt(numThreads)
  }

  def closeThread(threadId: Int): Unit = {
    lock.writeLock().lock()
    try {
      val newMsg: ForumThread = threads(threadId) match {
        case Left(msg) => Left(msg)
        case Right(msg) => Left(msg)
      }
      threads.update(threadId, newMsg)
    } finally {
      lock.writeLock().unlock()
    }
  }
}
