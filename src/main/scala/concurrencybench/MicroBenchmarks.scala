package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.annotations._

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
@State(Scope.Thread)
class Benchmarks {
  private var custForum: CustomForum = null
  private var stdForum: StandardForum = null

  @Setup
  def setup(): Unit = {
    custForum = new CustomForum()
    stdForum = new StandardForum()

    for (forum <- List(custForum, stdForum)) {
      for (_ <- 0 until 100) {
        forum.createThread()
      }

      for (_ <- 0 until 100) {
        val id = forum.getRandomThreadId()
        forum.replyToThread(id, "Hello")
      }

      for (i <- 0 until 10) {
        forum.closeThread(i)
      }
    }
  }

  @Threads(10)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  def customReadThread(): Option[String] = {
    val id = custForum.getRandomThreadId()
    custForum.readThread(id)
  }

  @Threads(10)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  def customReplyToThread(): Boolean = {
    val id = custForum.getRandomThreadId()
    custForum.replyToThread(id, "Hi")
  }

  @Threads(10)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  def customCloseThread(): Unit = {
    val id = custForum.getRandomThreadId()
    custForum.closeThread(id)
  }

  @Threads(10)
  @Benchmark
  def standardReadThread(): Option[String] = {
    val id = stdForum.getRandomThreadId()
    stdForum.readThread(id)
  }

  @Threads(10)
  @Benchmark
  def standardReplyToThread(): Boolean = {
    val id = stdForum.getRandomThreadId()
    stdForum.replyToThread(id, "Hi")
  }

  @Threads(10)
  @Benchmark
  def standardCloseThread(): Unit = {
    val id = stdForum.getRandomThreadId()
    stdForum.closeThread(id)
  }
}
