class TpcdsBenchmark(benchName: String)
    extends SparkBenchmark(benchName: String) {

  val inputPath = spark.conf.get("spark.tpcds.input.path")

  val queryNames = Seq(
    "q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "q9", "q10",
    "q11", "q12", "q13", "q14a", "q14b", "q15", "q16", "q17", "q18", "q19",
    "q20", "q21", "q22", "q23a", "q23b", "q24a", "q24b", "q25", "q26", "q27",
    "q28", "q29", "q30", "q31", "q32", "q33", "q34", "q35", "q36", "q37",
    "q38", "q39a", "q39b", "q40", "q41", "q42", "q43", "q44", "q45", "q46",
    "q47", "q48", "q49", "q50", "q51", "q52", "q53", "q54", "q55", "q56",
    "q57", "q58", "q59", "q60", "q61", "q62", "q63", "q64", "q65", "q66",
    "q67", "q68", "q69", "q70", "q71", "q72", "q73", "q74", "q75", "q76",
    "q77", "q78", "q79", "q80", "q81", "q82", "q83", "q84", "q85", "q86",
    "q87", "q88", "q89", "q90", "q91", "q92", "q93", "q94", "q95", "q96",
    "q97", "q98", "q99", "ss_max"
  )

  def prepareTables(queryId: String): Unit = {
    queryId match {
      case "q1" => createTables(Seq("store_returns", "date_dim", "store", "customer"))
      case "q2" => createTables(Seq("web_sales", "catalog_sales", "date_dim"))
      case "q3" => createTables(Seq("date_dim", "store_sales", "item"))
      case "q4" => createTables(Seq("customer", "store_sales", "date_dim", "catalog_sales", "web_sales"))
      case "q5" => createTables(Seq("store_sales", "store_returns", "catalog_sales",
        "catalog_returns", "date_dim", "web_site", "store", "catalog_page", "web_sales", "web_returns"))
      case "q6" => createTables(Seq("customer_address", "customer", "store_sales", "date_dim", "item"))
      case "q7" => createTables(Seq("store_sales", "customer_demographics", "date_dim", "item", "promotion"))
      case "q8" => createTables(Seq("store_sales", "date_dim", "store", "customer_address", "customer"))
      case "q9" => createTables(Seq("store_sales", "reason"))
      case "q10" =>
        createTables(Seq("customer", "customer_address", "customer_demographics",
          "store_sales", "date_dim", "web_sales", "catalog_sales"))
      case "q11" => createTables(Seq("customer", "store_sales", "date_dim", "web_sales"))
      case "q12" => createTables(Seq("web_sales", "item", "date_dim"))
      case "q13" => createTables(Seq("store_sales", "store", "customer_demographics",
        "household_demographics", "customer_address", "date_dim"))
      case "q14a" => createTables(Seq("item", "store_sales", "date_dim", "catalog_sales", "web_sales"))
      case "q14b" => createTables(Seq("item", "store_sales", "date_dim", "catalog_sales", "web_sales"))
      case "q15" => createTables(Seq("catalog_sales", "customer", "date_dim", "customer_address"))
      case "q16" => createTables(Seq("catalog_sales", "date_dim", "customer_address", "call_center", "catalog_returns"))
      case "q17" => createTables(Seq("store_sales", "store_returns", "catalog_sales", "date_dim", "store", "item"))
      case "q18" => createTables(Seq("catalog_sales", "customer_demographics", "customer",
        "customer_address", "date_dim", "item"))
      case "q19" => createTables(Seq("date_dim", "store_sales", "item", "customer", "customer_address", "store"))
      case "q20" => createTables(Seq("catalog_sales", "item", "date_dim"))
      case "q21" => createTables(Seq("inventory", "warehouse", "item", "date_dim"))
      case "q22" => createTables(Seq("inventory", "date_dim", "item", "warehouse"))
      case "q23a" => createTables(Seq("store_sales", "date_dim", "item", "customer", "catalog_sales", "web_sales"))
      case "q23b" => createTables(Seq("store_sales", "date_dim", "item", "customer", "catalog_sales", "web_sales"))
      case "q24a" => createTables(Seq("store_sales", "store_returns", "store", "item", "customer", "customer_address"))
      case "q24b" => createTables(Seq("store_sales", "store_returns", "store", "item", "customer", "customer_address"))
      case "q25" => createTables(Seq("store_sales", "store_returns", "catalog_sales", "date_dim", "store", "item"))
      case "q26" => createTables(Seq("catalog_sales", "customer_demographics", "date_dim", "item", "promotion"))
      case "q27" => createTables(Seq("store_sales", "customer_demographics", "date_dim", "store", "item"))
      case "q28" => createTables(Seq("store_sales"))
      case "q29" => createTables(Seq("store_sales", "store_returns", "catalog_sales", "date_dim", "store", "item"))
      case "q30" => createTables(Seq("web_returns", "date_dim", "customer_address", "customer"))
      case "q31" => createTables(Seq("store_sales", "date_dim", "customer_address", "web_sales"))
      case "q32" => createTables(Seq("catalog_sales", "item", "date_dim"))
      case "q33" => createTables(Seq("store_sales", "date_dim", "customer_address", "item",
        "catalog_sales", "web_sales"))
      case "q34" => createTables(Seq("store_sales", "date_dim", "store", "household_demographics", "customer"))
      case "q35" => createTables(Seq("customer", "customer_address", "customer_demographics",
        "store_sales", "date_dim", "web_sales", "catalog_sales"))
      case "q36" => createTables(Seq("store_sales", "date_dim", "item", "store"))
      case "q37" => createTables(Seq("item", "inventory", "date_dim", "catalog_sales"))
      case "q38" => createTables(Seq("store_sales", "date_dim", "customer", "catalog_sales", "web_sales"))
      case "q39a" => createTables(Seq("inventory", "item", "warehouse", "date_dim"))
      case "q39b" => createTables(Seq("inventory", "item", "warehouse", "date_dim"))
      case "q40" => createTables(Seq("catalog_sales", "warehouse", "item", "date_dim", "catalog_returns"))
      case "q41" => createTables(Seq("item"))
      case "q42" => createTables(Seq("date_dim", "store_sales", "item"))
      case "q43" => createTables(Seq("date_dim", "store_sales", "store"))
      case "q44" => createTables(Seq("store_sales", "item"))
      case "q45" => createTables(Seq("web_sales", "customer", "customer_address", "date_dim", "item"))
      case "q46" => createTables(Seq("store_sales", "date_dim", "store",
        "household_demographics", "customer_address", "customer"))
      case "q47" => createTables(Seq("item", "store_sales", "date_dim", "store"))
      case "q48" => createTables(Seq("store_sales", "store", "customer_demographics",
        "customer_address", "date_dim"))
      case "q49" => createTables(Seq("web_sales", "date_dim", "catalog_sales",
        "catalog_returns", "store_sales", "store_returns", "web_returns"))
      case "q50" => createTables(Seq("store_sales", "store_returns", "store", "date_dim"))
      case "q51" => createTables(Seq("web_sales", "date_dim", "store_sales"))
      case "q52" => createTables(Seq("date_dim", "store_sales", "item"))
      case "q53" => createTables(Seq("item", "store_sales", "date_dim", "store"))
      case "q54" => createTables(Seq("catalog_sales", "web_sales", "store_sales",
        "customer_address", "store", "date_dim", "item", "customer"))
      case "q55" => createTables(Seq("date_dim", "store_sales", "item"))
      case "q56" => createTables(Seq("store_sales", "date_dim", "customer_address", "item",
        "catalog_sales", "web_sales"))
      case "q57" => createTables(Seq("item", "catalog_sales", "date_dim", "call_center"))
      case "q58" => createTables(Seq("store_sales", "item", "date_dim", "catalog_sales", "web_sales"))
      case "q59" => createTables(Seq("store_sales", "date_dim", "store"))
      case "q60" => createTables(Seq("store_sales", "date_dim", "customer_address",
        "item", "catalog_sales", "web_sales"))
      case "q61" => createTables(Seq("store_sales", "store", "promotion", "date_dim",
        "customer", "customer_address", "item"))
      case "q62" => createTables(Seq("web_sales", "warehouse", "ship_mode", "web_site", "date_dim"))
      case "q63" => createTables(Seq("item", "store_sales", "date_dim", "store"))
      case "q64" =>
        createTables(Seq("catalog_sales", "catalog_returns", "store_sales", "store_returns", "date_dim",
          "store", "customer", "customer_demographics", "promotion", "household_demographics", "customer_address",
          "income_band", "item"))
      case "q65" => createTables(Seq("store", "item", "store_sales", "date_dim"))
      case "q66" => createTables(Seq("web_sales", "warehouse", "date_dim", "time_dim", "ship_mode", "catalog_sales"))
      case "q67" => createTables(Seq("store_sales", "date_dim", "store", "item"))
      case "q68" => createTables(Seq("store_sales", "date_dim", "store",
        "household_demographics", "customer_address", "customer"))
      case "q69" => createTables(Seq("store_sales", "date_dim", "customer",
        "web_sales", "catalog_sales", "customer_address", "customer_demographics"))
      case "q70" => createTables(Seq("store_sales", "date_dim", "store"))
      case "q71" => createTables(Seq("item", "web_sales", "date_dim", "catalog_sales", "store_sales", "time_dim"))
      case "q72" => createTables(Seq("catalog_sales", "inventory", "warehouse", "item", "customer_demographics",
        "household_demographics", "date_dim", "promotion", "catalog_returns"))
      case "q73" => createTables(Seq("store_sales", "date_dim", "store", "household_demographics", "customer"))
      case "q74" => createTables(Seq("customer", "store_sales", "date_dim", "web_sales"))
      case "q75" => createTables(Seq("catalog_sales", "item", "date_dim", "catalog_returns", "store_sales",
        "store_returns", "web_sales", "web_returns"))
      case "q76" => createTables(Seq("store_sales", "item", "date_dim", "web_sales", "catalog_sales"))
      case "q77" => createTables(Seq("store_sales", "date_dim", "store", "store_returns",
        "catalog_sales", "catalog_returns", "web_sales", "web_page", "web_returns"))
      case "q78" => createTables(Seq("web_sales", "web_returns", "catalog_sales",
        "catalog_returns", "date_dim", "store_sales", "store_returns"))
      case "q79" => createTables(Seq("store_sales", "date_dim", "store", "household_demographics", "customer"))
      case "q80" => createTables(Seq("store_sales", "store_returns", "date_dim", "store", "item", "promotion",
        "catalog_sales", "catalog_returns", "catalog_page", "web_sales", "web_returns", "web_site"))
      case "q81" => createTables(Seq("catalog_returns", "date_dim", "customer_address", "customer"))
      case "q82" => createTables(Seq("item", "inventory", "date_dim", "store_sales"))
      case "q83" => createTables(Seq("store_sales", "item", "store_returns", "date_dim", "catalog_returns", "web_returns"))
      case "q84" => createTables(Seq("customer", "customer_address", "customer_demographics",
        "household_demographics", "income_band", "store_returns"))
      case "q85" => createTables(Seq("web_sales", "web_returns", "web_page", "customer_demographics",
        "customer_address", "date_dim", "reason"))
      case "q86" => createTables(Seq("web_sales", "date_dim", "item"))
      case "q87" => createTables(Seq("store_sales", "date_dim", "customer", "catalog_sales", "customer", "web_sales"))
      case "q88" => createTables(Seq("store_sales", "household_demographics", "time_dim", "store"))
      case "q89" => createTables(Seq("item", "store_sales", "date_dim", "store"))
      case "q90" => createTables(Seq("web_sales", "household_demographics", "time_dim", "web_page"))
      case "q91" => createTables(Seq("call_center", "catalog_returns", "date_dim",
        "customer", "customer_address", "customer_demographics", "household_demographics"))
      case "q92" => createTables(Seq("web_sales", "item", "date_dim"))
      case "q93" => createTables(Seq("store_sales", "store_returns", "reason"))
      case "q94" => createTables(Seq("web_sales", "date_dim", "customer_address",
        "web_site", "web_sales", "web_returns"))
      case "q95" => createTables(Seq("web_sales", "date_dim", "customer_address", "web_site", "web_returns"))
      case "q96" => createTables(Seq("store_sales", "household_demographics", "time_dim", "store"))
      case "q97" => createTables(Seq("store_sales", "date_dim", "catalog_sales"))
      case "q98" => createTables(Seq("store_sales", "item", "date_dim"))
      case "q99" => createTables(Seq("catalog_sales", "warehouse", "ship_mode", "call_center", "date_dim"))
      case "ss_max" => createTables(Seq("store_sales"))
    }
  }

  def createTables(tableNames: Seq[String]): Unit = {
    tableNames.foreach { tableName =>
      spark.read.parquet(s"$inputPath/$tableName")
        .createOrReplaceTempView(tableName)
    }
  }
}
