name := """user-demo"""
organization := "com.sanjay"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.8",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "org.postgresql" % "postgresql" % "42.2.24",
  "com.typesafe.play" %% "play-json" % "2.9.2"
)
