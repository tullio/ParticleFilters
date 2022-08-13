val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    assemblyPackageScala / assembleArtifact := false,
    //assemblyPackageDependency / assembleArtifact := false,

    assemblyPackageDependency / assemblyOption ~= {
      _.withIncludeDependency(true).withIncludeScala(false)
    },

    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies += "org.nd4j" % "nd4j-api" % "1.0.0-M1.1",
    libraryDependencies += "org.nd4j" % "nd4j" % "1.0.0-M1.1",
    libraryDependencies += "org.nd4j" % "nd4j-backend-impls" % "1.0.0-M1.1",
    libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-M1.1",
    //libraryDependencies +=  "org.jfree" % "jfreechart" % "1.5.3",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.9",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    //libraryDependencies += "org.scalanlp" %% "breeze-viz" % "2.0-RC3",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.8",
    libraryDependencies += "org.tinylog" % "tinylog" % "1.3.6",
    libraryDependencies += "org.tinylog" % "tinylog-api" % "2.1.2",
    libraryDependencies += "org.tinylog" % "tinylog-impl" % "2.1.2",
    libraryDependencies += "com.electronwill.night-config" %  "toml" % "3.6.5",
    libraryDependencies += ("com.github.pathikrit" %% "better-files" % "3.9.1").cross(CrossVersion.for3Use2_13),
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.30.0",

  )
assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList("javax", "xml", xs @ _*)         => MergeStrategy.first
    case PathList("com", "fasterxml", xs @ _*)         => MergeStrategy.first
    case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("com", "google", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", "services", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
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
    case PathList("module-info.class")                                => MergeStrategy.discard
    // Failed
    case "org.apache.hadoop.fs.FileSystem"                                => MergeStrategy.discard
    // Great!
    case PathList(p @ _*) if p.last == "org.apache.hadoop.fs.FileSystem" => MergeStrategy.discard
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

assemblyMergeStrategy in assemblyPackageDependency := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList("javax", "xml", xs @ _*)         => MergeStrategy.first
    case PathList("com", "fasterxml", xs @ _*)         => MergeStrategy.first
    case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("com", "google", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", "services", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case PathList("scala", xs @ _*) => MergeStrategy.discard
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
    case PathList("module-info.class")                                => MergeStrategy.discard
    // Failed
    case "org.apache.hadoop.fs.FileSystem"                                => MergeStrategy.discard
    // Great!
    case PathList(p @ _*) if p.last == "org.apache.hadoop.fs.FileSystem" => MergeStrategy.discard
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
