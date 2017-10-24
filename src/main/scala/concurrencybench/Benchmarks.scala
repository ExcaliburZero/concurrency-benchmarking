package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch

//@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.All))
class Benchmarks {

  @Benchmark
  def standard(blackhole: Blackhole): Unit = {
    val forum = new StandardForum()
    simulation(forum, blackhole)
  }

  @Benchmark
  def custom(blackhole: Blackhole): Unit = {
    val forum = new CustomForum()
    simulation(forum, blackhole)
  }

  def simulation(forum: Forum, blackhole: Blackhole): Unit = {
    forum.createThread()

    val numClients = 100//1000//25
    val start = 1
    val moderatorRatio = 5
    val duration = 10//200

    val countdown = new CountDownLatch(numClients)

    val clients = for (i <- start to start + numClients)
      yield if (i % moderatorRatio == 0) new Moderator(forum, countdown, duration, i, blackhole)
        else new User(forum, countdown, duration, i, blackhole)

    for (c <- clients) {
      val thread = new Thread {
        override def run {
          c.start()
        }
      }
      thread.start()
    }
    countdown.await()
  }
}
