object QueryRunner {

  def main(args: Array[String]): Unit = {
    assert(args.length == 2)

    val benchName = args(0)

    val benchmark = if (benchName == "imdb") {
      new ImdbBenchmark(benchName)
    } else if (benchName == "tpcds") {
      new TpcdsBenchmark(benchName)
    } else {
      throw new UnsupportedOperationException(s"Benchmark $benchName not supported")
    }

    val queryId = benchmark.queryNames(args(1).toInt)
    println(s"Executing query $queryId / ${benchmark.queryNames.size}")

    benchmark.executeQuery(queryId)
  }
}
