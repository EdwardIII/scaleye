val scala3Version = "3.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scaleye",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.softwaremill.sttp.client3" %% "core" % "3.4.1"
    )
  )
