class ImdbBenchmark(benchName: String)
    extends SparkBenchmark(benchName: String) {

  val inputPath = spark.conf.get("spark.imdb.input.path")

  val queryNames = Seq(
    "1a", "1b", "1c", "1d",
    "2a", "2b", "2c", "2d",
    "3a", "3b", "3c",
    "4a", "4b", "4c",
    "5a", "5b", "5c",
    "6a", "6b", "6c", "6d", "6e",
    "7a", "7b", "7c",
    "8a", "8b", "8c", "8d",
    "9a", "9b", "9c", "9d",
    "10a", "10b", "10c",
    "11a", "11b", "11c", "11d",
    "12a", "12b", "12c",
    "13a", "13b", "13c", "13d",
    "14a", "14b", "14c",
    "15a", "15b", "15c", "15d",
    "16a", "16b", "16c", "16d",
    "17a", "17b", "17c", "17d", "17e", "17f",
    "18a", "18b", "18c",
    "19a", "19b", "19c",
    "20a", "20b", "20c",
    "21a", "21b", "21c",
    "22a", "22b", "22c", "22d",
    "23a", "23b", "23c",
    "24a", "24b",
    "25a", "25b", "25c",
    "26a", "26b", "26c",
    "27a", "27b", "27c",
    "28a", "28b", "28c",
    "29a", "29b", "29c",
    "30a", "30b", "30c",
    "31a", "31b", "31c",
    "32a", "32b",
    "33a", "33b", "33c")

  def prepareTables(queryId: String): Unit = {
    queryId match {
      case "1a" | "1b" | "1c" | "1d" =>
        createTables(Seq("company_type", "info_type", "movie_companies", "movie_info_idx", "title"))
      case "2a" | "2b" | "2c" | "2d" =>
        createTables(Seq("company_name", "keyword", "movie_companies", "movie_keyword", "title"))
      case "3a" | "3b" | "3c" =>
        createTables(Seq("keyword", "movie_info", "movie_keyword", "title"))
      case "4a" | "4b" | "4c" =>
        createTables(Seq("info_type", "keyword", "movie_info_idx", "movie_keyword", "title"))
      case "5a" | "5b" | "5c" =>
        createTables(Seq("company_type", "info_type", "movie_companies", "movie_info", "title"))
      case "6a" | "6b" | "6c" | "6d" | "6e" | "6f" =>
        createTables(Seq("cast_info", "keyword", "movie_keyword", "name", "title"))
      case "7a" | "7b" | "7c" =>
        createTables(Seq("aka_name", "cast_info", "info_type", "link_type", "movie_link", "name", "person_info", "title"))
      case "8a" | "8b" | "8c" | "8d" =>
        createTables(Seq("aka_name", "cast_info", "company_name", "movie_companies", "name", "role_type", "title"))
      case "9a" | "9b" | "9c" | "9d" =>
        createTables(Seq("aka_name", "char_name", "cast_info", "company_name", "movie_companies", "name", "role_type", "title"))
      case "10a" | "10b" | "10c" =>
        createTables(Seq("char_name", "cast_info", "company_name", "company_type", "movie_companies", "role_type", "title"))
      case "11a" | "11b" | "11c" | "11d" =>
        createTables(Seq("company_name", "company_type", "keyword", "link_type", "movie_companies", "movie_keyword", "movie_link", "title"))
      case "12a" | "12b" | "12c" =>
        createTables(Seq("company_name", "company_type", "info_type", "movie_companies", "movie_info", "movie_info_idx", "title"))
      case "13a" | "13b" | "13c" | "13d" =>
        createTables(Seq("company_name", "company_type", "info_type", "kind_type", "movie_companies", "movie_info", "movie_info_idx", "title"))
      case "14a" | "14b" | "14c" =>
        createTables(Seq("info_type", "keyword", "kind_type", "movie_info", "movie_info_idx", "movie_keyword", "title"))
      case "15a" | "15b" | "15c" | "15d" =>
        createTables(Seq("aka_title", "company_name", "company_type", "info_type", "keyword", "movie_companies", "movie_info", "movie_keyword", "title"))
      case "16a" | "16b" | "16c" | "16d" =>
        createTables(Seq("aka_name", "cast_info", "company_name", "keyword", "movie_companies", "movie_keyword", "name", "title"))
      case "17a" | "17b" | "17c" | "17d" | "17e" | "17f" =>
        createTables(Seq("cast_info", "company_name", "keyword", "movie_companies", "movie_keyword", "name", "title"))
      case "18a" | "18b" | "18c" =>
        createTables(Seq("cast_info", "info_type", "movie_info", "movie_info_idx", "name", "title"))
      case "19a" | "19b" | "19c" | "19d" =>
        createTables(Seq("aka_name", "char_name", "cast_info", "company_name", "info_type", "movie_companies", "movie_info", "name", "role_type", "title"))
      case "20a" | "20b" | "20c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "char_name", "cast_info", "keyword", "kind_type", "movie_keyword", "name", "title"))
      case "21a" | "21b" | "21c" =>
        createTables(Seq("company_name", "company_type", "keyword", "link_type", "movie_companies", "movie_info", "movie_keyword", "movie_link", "title"))
      case "22a" | "22b" | "22c" | "22d" =>
        createTables(Seq("company_name", "company_type", "info_type", "keyword", "kind_type", "movie_companies", "movie_info", "movie_info_idx", "movie_keyword", "title"))
      case "23a" | "23b" | "23c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "company_name", "company_type", "info_type", "keyword", "kind_type", "movie_companies", "movie_info", "movie_keyword", "title"))
      case "24a" | "24b" =>
        createTables(Seq("aka_name", "char_name", "cast_info", "company_name", "info_type", "keyword", "movie_companies", "movie_info", "movie_keyword", "name", "role_type", "title"))
      case "25a" | "25b" | "25c" =>
        createTables(Seq("cast_info", "info_type", "keyword", "movie_info", "movie_info_idx", "movie_keyword", "name", "title"))
      case "26a" | "26b" | "26c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "char_name", "cast_info", "info_type", "keyword", "kind_type", "movie_info_idx", "movie_keyword", "name", "title"))
      case "27a" | "27b" | "27c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "company_name", "company_type", "keyword", "link_type", "movie_companies", "movie_info", "movie_keyword", "movie_link", "title"))
      case "28a" | "28b" | "28c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "company_name", "company_type", "info_type", "keyword", "kind_type", "movie_companies", "movie_info", "movie_info_idx", "movie_keyword", "title"))
      case "29a" | "29b" | "29c" =>
        createTables(Seq("aka_name", "complete_cast", "comp_cast_type", "char_name", "cast_info", "company_name", "info_type", "keyword", "movie_companies", "movie_info", "movie_keyword", "name", "person_info", "role_type", "title"))
      case "30a" | "30b" | "30c" =>
        createTables(Seq("complete_cast", "comp_cast_type", "cast_info", "info_type", "keyword", "movie_info", "movie_info_idx", "movie_keyword", "name", "title"))
      case "31a" | "31b" | "31c" =>
        createTables(Seq("cast_info", "company_name", "info_type", "keyword", "movie_companies", "movie_info", "movie_info_idx", "movie_keyword", "name", "title"))
      case "32a" | "32b" =>
        createTables(Seq("keyword", "link_type", "movie_keyword", "movie_link", "title"))
      case "33a" | "33b" | "33c" =>
        createTables(Seq("company_name", "info_type", "kind_type", "link_type", "movie_companies", "movie_info_idx", "movie_link", "title"))
     }
  }

  def createTables(tableNames: Seq[String]): Unit = {
    tableNames.foreach {
      case "aka_name" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/aka_name.csv`")
          .toDF("id", "person_id", "name", "imdb_index", "name_pcode_f",
            "name_pcode_nf", "surname_pcode", "md5sum").createOrReplaceTempView("aka_name")
      case "aka_title" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/aka_title.csv`")
          .toDF("id", "movie_id", "title", "imdb_index", "kind_id",
            "production_year", "phonetic_code", "episode_of_id", "season_nr",
            "episode_nr", "note", "md5sum").createOrReplaceTempView("aka_title")
      case "cast_info" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/cast_info.csv`")
          .toDF("id", "person_id", "movie_id", "person_role_id", "note",
            "nr_order", "role_id").createOrReplaceTempView("cast_info")
      case "char_name" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/char_name.csv`")
          .toDF("id", "name", "imdb_index", "imdb_id", "name_pcode_nf",
            "surname_pcode", "md5sum").createOrReplaceTempView("char_name")
      case "comp_cast_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/comp_cast_type.csv`")
          .toDF("id", "kind").createOrReplaceTempView("comp_cast_type")
      case "company_name" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/company_name.csv`")
          .toDF("id", "name", "country_code", "imdb_id", "name_pcode_nf",
            "name_pcode_sf", "md5sum").createOrReplaceTempView("company_name")
      case "company_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/company_type.csv`")
          .toDF("id", "kind").createOrReplaceTempView("company_type")
      case "complete_cast" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/complete_cast.csv`")
          .toDF("id", "movie_id", "subject_id", "status_id")
          .createOrReplaceTempView("complete_cast")
      case "info_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/info_type.csv`")
          .toDF("id", "info").createOrReplaceTempView("info_type")
      case "keyword" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/keyword.csv`")
          .toDF("id", "keyword", "phonetic_code").createOrReplaceTempView("keyword")
      case "kind_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/kind_type.csv`")
          .toDF("id", "kind").createOrReplaceTempView("kind_type")
      case "link_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/link_type.csv`")
          .toDF("id", "link").createOrReplaceTempView("link_type")
      case "movie_companies" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/movie_companies.csv`")
          .toDF("id", "movie_id", "company_id", "company_type_id", "note")
          .createOrReplaceTempView("movie_companies")
      case "movie_info" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/movie_info.csv`")
          .toDF("id", "movie_id", "info_type_id", "info", "note")
          .createOrReplaceTempView("movie_info")
      case "movie_info_idx" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/movie_info_idx.csv`")
          .toDF("id", "movie_id", "info_type_id", "info", "note")
          .createOrReplaceTempView("movie_info_idx")
      case "movie_keyword" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/movie_keyword.csv`")
          .toDF("id", "movie_id", "keyword_id")
          .createOrReplaceTempView("movie_keyword")
      case "movie_link" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/movie_link.csv`")
          .toDF("id", "movie_id", "linked_movie_id", "link_type_id")
          .createOrReplaceTempView("movie_link")
      case "name" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/name.csv`")
          .toDF("id", "name", "imdb_index", "imdb_id", "gender",
            "name_pcode_cf", "name_pcode_nf", "surname_pcode", "md5sum")
          .createOrReplaceTempView("name")
      case "person_info" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/person_info.csv`")
          .toDF("id", "person_id", "info_type_id", "info", "note")
          .createOrReplaceTempView("person_info")
      case "role_type" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/role_type.csv`")
          .toDF("id", "role").createOrReplaceTempView("role_type")
      case "title" =>
        spark.sql(s"SELECT * FROM csv.`$inputPath/title.csv`")
          .toDF("id", "title", "imdb_index", "kind_id", "production_year",
            "imdb_id", "phonetic_code", "episode_of_id", "season_nr",
            "episode_nr", "series_years", "md5sum")
          .createOrReplaceTempView("title")
    }
  }
}
