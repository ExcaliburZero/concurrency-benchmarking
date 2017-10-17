package concurrencybench

import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.CountDownLatch

abstract class Client(forum: Forum) {
  def start(): Unit
}

class User(forum: Forum, countdown: CountDownLatch, limit: Int, interval: Int, blackhole: Blackhole) extends Client(forum) {
  val interval2: Int = 100

  var count: Int = 1

  def start(): Unit = {
    //println("Client start: " + interval)
    var continue = true
    while (continue) {
      val action = count % interval

      if (action == 0) {
        if ((count / interval) % interval2 == 0) {
          forum.createThread()
        } else {
          val threadId = forum.getRandomThreadId()
          forum.replyToThread(threadId, count.toString)
        }
      } else {
        val threadId = forum.getRandomThreadId()
        val msg = forum.readThread(threadId)
        blackhole.consume(msg)
        //println(msg)
      }

      Thread.sleep(100 + action)

      if (count > limit) {
        continue = false
      } else {
        count += 1
      }
    }
    countdown.countDown()
  }
}
