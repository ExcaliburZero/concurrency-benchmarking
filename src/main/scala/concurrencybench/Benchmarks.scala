package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

import java.io.File

import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch

//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@BenchmarkMode(Array(Mode.All))
@BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime))
@State(Scope.Benchmark)
class Benchmarks {
  val delimiter = "<\\*>\\(<\\*>\\)<\\*>"
  val wordLimit = 100
  val userFiles = new File("users").listFiles

  val chains: Array[(String, MarkovChain)] = for (f <- userFiles) yield loadUser(f)

  for ((u, mc) <- chains) {
    //println(u + ": " + mc.generateSentence())
  }

  def loadUser(userFile: File): (String, MarkovChain) = {
    val mc = new MarkovChain(wordLimit)
    //mc.addFromFile(userFile)
    // Split file on delimiters and then add all
    val source = scala.io.Source.fromFile(userFile)
    val lines: String = try source.getLines mkString "\n" finally source.close()
    val sentences = lines.split(delimiter)
    //for (s <- sentences) yield println(s)
    //println(sentences.length)
    for (s <- sentences) yield mc.addSentence(s)
    (userFile.toString.dropRight(4), mc)
  }

  @Benchmark
  def standard5(blackhole: Blackhole): Unit = {
    val forum = new StandardForum()
    simulation(forum, blackhole, 5)
  }

  @Benchmark
  def custom5(blackhole: Blackhole): Unit = {
    val forum = new CustomForum()
    simulation(forum, blackhole, 5)
  }

  @Benchmark
  def standard10(blackhole: Blackhole): Unit = {
    val forum = new StandardForum()
    simulation(forum, blackhole, 10)
  }

  @Benchmark
  def custom10(blackhole: Blackhole): Unit = {
    val forum = new CustomForum()
    simulation(forum, blackhole, 10)
  }

  def simulation(forum: Forum, blackhole: Blackhole, numClients: Int): Unit = {
    forum.createThread()

    //val numClients = 10//100//1000//25
    val start = 1
    val moderatorRatio = 5
    val duration = 150//200

    val countdown = new CountDownLatch(numClients)

    val clients = for (i <- start to start + numClients)
      yield if (i % moderatorRatio == 0) new Moderator(forum, countdown, duration, i, blackhole, chains(i - 1)._1, chains(i - 1)._2)
        else new User(forum, countdown, duration, i, blackhole, chains(i - 1)._1, chains(i - 1)._2)

    for (c <- clients) {
      val thread = new Thread {
        override def run {
          c.start()
        }
      }
      thread.start()
    }
    countdown.await()

    /*var continue = true
    while (continue) {
      val msg = forum.readThread(forum.getRandomThreadId())

      msg match {
        case Some(m) =>
          continue = false
          println(m)
        case None =>
          ()
      }
    }*/
  }
}
