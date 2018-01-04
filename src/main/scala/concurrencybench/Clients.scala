package concurrencybench

import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.CountDownLatch

abstract class Client(forum: Forum) {
  def start(): Unit
}

class User(forum: Forum, countdown: CountDownLatch, limit: Int, interval: Int, blackhole: Blackhole, username: String, mc: MarkovChain) extends Client(forum) {
  val interval2: Int = 8

  var count: Int = 1

  def start(): Unit = {
    var continue = true
    while (continue) {
      val action = count % interval

      if (action == 0) {
        if ((count / interval) % interval2 == 0) {
          forum.createThread()
        } else {
          val threadId = forum.getRandomThreadId()
          val succeeded = forum.replyToThread(threadId, "\n~~~~~~~~~~~~~~~~~~~~~~~\n" + username + "\n" + mc.generateSentence())
        }
      } else {
        var readThread = false
        while (!readThread) {
          val threadId = forum.getRandomThreadId()
          val msg = forum.readThread(threadId)
          msg match {
            case Some(m) =>
              blackhole.consume(msg)
              readThread = true
            case None => ()
          }
        }
      }

      Thread.sleep((100 + action + Math.abs((interval + count).toString.hashCode % 200)) / 10)

      if (count > limit) {
        continue = false
      } else {
        count += 1
      }
    }
    countdown.countDown()
  }
}

class Moderator(forum: Forum, countdown: CountDownLatch, limit: Int, interval: Int, blackhole: Blackhole, username: String, mc: MarkovChain) extends Client(forum) {
  val interval2: Int = 2

  var count: Int = 1

  def start(): Unit = {
    var continue = true
    while (continue) {
      val action = count % interval

      if (action == 0) {
        if ((count / interval) % interval2 == 0) {
          val threadId = forum.getRandomThreadId()
          forum.closeThread(threadId)
        } else {
          val threadId = forum.getRandomThreadId()
          val succeeded = forum.replyToThread(threadId, mc.generateSentence())
        }
      } else if (action == 1 && count % 5 == 0) {
      } else {
        var readThread = false
        while (!readThread) {
          val threadId = forum.getRandomThreadId()
          val msg = forum.readThread(threadId)
          msg match {
            case Some(m) =>
              blackhole.consume(msg)
              readThread = true
            case None => ()
          }
        }
      }

      Thread.sleep((100 + action + Math.abs((interval + count).toString.hashCode % 200)) / 10)

      if (count > limit) {
        continue = false
      } else {
        count += 1
      }
    }
    countdown.countDown()
  }
}
