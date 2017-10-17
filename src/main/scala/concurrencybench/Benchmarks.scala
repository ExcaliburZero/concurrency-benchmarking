package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch

//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@BenchmarkMode(Array(Mode.Throughput))
class Benchmarks {

  @Benchmark
  def benchmark(blackhole: Blackhole): Unit = {
    val forum = new StandardForum()
    forum.createThread()

    val numClients = 10
    val start = 1000

    val countdown = new CountDownLatch(numClients)

    val clients = for (i <- start to start + numClients) yield new User(forum, countdown, 20, i, blackhole)

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
