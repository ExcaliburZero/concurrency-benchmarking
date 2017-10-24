# Concurrency Benchmarking
These are some tests to benchmark some concurrent data structures.

## JMH
To run the JMH benchmarks, run the following command:

```
sbt "jmh:run -i 5 -wi 5 -t1 -rf csv -rff results.csv"
```

## Plots
Once you have the results from running JMH, you can create the plots  by running the following command:

```
mkdir plots
python3 plot.py
```
