name := "hdsl"

organization := "com.github.chengpohi"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.1"

unmanagedBase := baseDirectory.value / "lib"

resolvers += Resolver.mavenLocal

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "json4s-jackson" % "3.5.0"
)

lazy val compileScalaStyle = taskKey[Unit]("compileScalastyle")

compileScalaStyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(scalastyleConfig in Compile) := file("project/scalastyle-config.xml")
(scalastyleConfig in Test) := file("project/scalastyle-test-config.xml")

(compile in Compile) <<= (compile in Compile) dependsOn compileScalaStyle
(test in Test) <<= (test in Test) dependsOn compileScalaStyle
