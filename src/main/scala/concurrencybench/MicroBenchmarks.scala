package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime))
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

  @Benchmark
  def customReadThread(): Option[String] = {
    val id = custForum.getRandomThreadId()
    custForum.readThread(id)
  }

  @Benchmark
  def customReplyToThread(): Boolean = {
    val id = custForum.getRandomThreadId()
    custForum.replyToThread(id, "Hi")
  }

  @Benchmark
  def customCreateThread(): Unit = {
    custForum.createThread()
  }

  @Benchmark
  def customCloseThread(): Unit = {
    val id = custForum.getRandomThreadId()
    custForum.closeThread(id)
  }

  @Benchmark
  def standardReadThread(): Option[String] = {
    val id = stdForum.getRandomThreadId()
    stdForum.readThread(id)
  }

  @Benchmark
  def standardReplyToThread(): Boolean = {
    val id = stdForum.getRandomThreadId()
    stdForum.replyToThread(id, "Hi")
  }

  @Benchmark
  def standardCreateThread(): Unit = {
    stdForum.createThread()
  }

  @Benchmark
  def standardCloseThread(): Unit = {
    val id = stdForum.getRandomThreadId()
    stdForum.closeThread(id)
  }
}
