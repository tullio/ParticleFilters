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
assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList("javax", "xml", xs @ _*)         => MergeStrategy.first
    case PathList("com", "fasterxml", xs @ _*)         => MergeStrategy.first
    case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("com", "google", xs @ _*) => MergeStrategy.last
    case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".json" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith "epoll_x86_64.so" => MergeStrategy.first
    case "UnusedStubClass.class"  => MergeStrategy.first
    case "jetty-dir.css"                            => MergeStrategy.first

    case "application.conf"                            => MergeStrategy.concat
    case "unwanted.txt"                                => MergeStrategy.discard
    // Failed
    case "org.apache.hadoop.fs.FileSystem"                                => MergeStrategy.discard
    // Great!
    case PathList(p @ _*) if p.last == "org.apache.hadoop.fs.FileSystem" => MergeStrategy.discard
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
