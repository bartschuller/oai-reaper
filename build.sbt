
name := "oai-reaper"

organization := "org.smop"

version := "0.2-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.7.8",
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"
)
