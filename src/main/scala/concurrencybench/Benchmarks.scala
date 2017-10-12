package concurrencybench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class Benchmarks {

  @Benchmark
  def benchmark(blackhole: Blackhole): Unit = {
    var a = 0
    for (i <- 0 to 100000) {
      if (i % 5 == 0 && i % 200 != 0) {
        a += 2
      }
    }
    blackhole.consume(a);
  }
}
