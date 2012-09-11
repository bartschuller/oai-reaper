
name := "oai-reaper"

organization := "org.smop"

version := "0.2-SNAPSHOT"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.8",
  "org.scala-tools.testing" % "specs_2.9.1" % "1.6.9" % "test"
)
