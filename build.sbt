val scala3Version = "3.0.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies += "org.nd4j" % "nd4j-api" % "1.0.0-M1.1",
    libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-M1.1",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.9",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    libraryDependencies += "org.scalanlp" %% "breeze-viz" % "2.0-RC3",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.8"


  )
