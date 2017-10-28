package concurrencybench

import java.util.concurrent.locks.ReentrantLock

class ReadWriteLock {
  private var numReaders: Int = 0
  private var numWriters: Int = 0

  private var waitingReaders: Int = 0
  private var waitingWriters: Int = 0

  private val lock = new ReentrantLock()
  private val readCondition = lock.newCondition()
  private val writeCondition = lock.newCondition()

  def lockRead(): Unit = {
    lock.lock()
    try {
      while (true) {
        if (numWriters == 0 && waitingWriters == 0) {
          numReaders += 1
          return
        }
        waitingReaders += 1
        readCondition.await()
        waitingReaders -= 1
      }
    } finally {
      lock.unlock()
    }
  }

  def lockWrite(): Unit = {
    lock.lock()
    try {
      while (true) {
        if (numWriters == 0 && numReaders == 0) {
          numWriters += 1
          return
        }
        waitingWriters += 1
        writeCondition.await()
        waitingWriters -= 1
      }
    } finally {
      lock.unlock()
    }
  }

  def unlockRead(): Unit = {
    lock.lock()
    try {
      numReaders -= 1
      if (numReaders == 0) {
        if (waitingWriters > 0) {
          writeCondition.signal()
        } else {
          readCondition.signalAll()
        }
      }
    } finally {
      lock.unlock()
    }
  }

  def unlockWrite(): Unit = {
    lock.lock()
    try {
      numWriters -= 1
      if (numWriters == 0) {
        if (waitingWriters > 0) {
          writeCondition.signal()
        } else {
          readCondition.signalAll()
        }
      }
    } finally {
      lock.unlock()
    }
  }
}
