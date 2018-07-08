import Dependencies._

lazy val root = (project in file(".")).
  settings(
    crossScalaVersions := Seq("2.11.11", "2.12.6"),
    scalacOptions += "-Ypartial-unification",
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "awsio",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.1.0",
      "org.typelevel" %% "cats-effect" % "1.0.0-RC2",
      "software.amazon.awssdk" % "sqs" % "2.0.0-preview-10",
      "software.amazon.awssdk" % "s3" % "2.0.0-preview-10",
      "software.amazon.awssdk" % "sns" % "2.0.0-preview-10",
      scalaTest % Test
    )
  )
