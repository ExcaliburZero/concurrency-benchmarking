package concurrencybench

import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.ThreadLocalRandom

import scala.collection.mutable.ArrayBuffer

class StandardForum extends Forum {
  val lock = new ReentrantReadWriteLock()
  val threads = new ArrayBuffer[String]()

  def readThread(threadId: Int): String = {
    lock.readLock().lock()
    val msg = try {
      threads(threadId)
    } finally {
      lock.readLock().unlock()
    }

    msg
  }

  def replyToThread(threadId: Int, msg: String): Unit = {
    lock.writeLock().lock()
    try {
      threads.update(threadId, msg)
    } finally {
      lock.writeLock().unlock()
    }
  }

  def createThread(): Unit = {
    lock.writeLock().lock()
    try {
      threads += "New Thread\n"
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
}
