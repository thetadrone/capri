name := "bribesched"

version := "0.1"

scalaVersion := "2.12.6"

mainClass in (Compile, run) := Some("scheduler.SpotManager")

unmanagedJars in Compile += file("lib/capri.jar")

libraryDependencies += "junit" % "junit" % "4.10" % Test

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"

libraryDependencies += "io.kubernetes" % "client-java" % "4.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

