import org.apache.commons.io.IOUtils

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.catalyst.analysis.UnresolvedRelation

abstract class SparkBenchmark(benchName: String) {

  val spark = SparkSession.builder().appName("SparkBenchmark").getOrCreate()

  def queryNames: Seq[String]

  def prepareTables(queryId: String)

  def executeQuery(queryId: String): Unit = {
    // spark.conf.set("spark.io.compression.codec", "snappy")
    prepareTables(queryId)

    val queryContent: String = IOUtils.toString(getClass()
      .getClassLoader().getResourceAsStream(s"$benchName/$queryId.sql"))
      .split(";").head

    spark.sql(s"$queryContent").count()
    spark.stop()
  }
}
