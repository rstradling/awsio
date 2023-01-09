import Dependencies._
import ReleaseTransformations._
import scala.xml.Elem
import scala.xml.transform.{RewriteRule, RuleTransformer}


val catsVersion = "2.9.0"
val catsEffectVersion = "3.4.4"
val awsVersion = "2.19.8"
val fs2Version = "3.4.0"

val sharedSettings = Seq(
  organization := "com.github.rstradling",
  crossScalaVersions := Seq("2.13.10", "3.2.1"),
  scalaVersion := "3.2.1",
  scalacOptions ++= {
    /*"-Xfatal-warnings",*/
    if (scalaVersion.value.startsWith("3")) Seq(
      "-Werror",
      "-Ykind-projector",
      "-deprecation"
    )
    else Seq(
        "-deprecation",
        "-encoding",
        "UTF-8",
        "-feature",
        "-language:higherKinds",
        "-release:11",
        "-unchecked",
        "-Ywarn-value-discard",
        "-Ywarn-numeric-widen"
    )
  },
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    uTest % Test
  ),
  organization := "com.github.rstradling",
  releaseCrossBuild := true,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("+publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  ),
  ThisBuild / isSnapshot := version.value endsWith "SNAPSHOT",
  publishTo := Some(
    if (isSnapshot.value) {
      Opts.resolver.sonatypeOssSnapshots.head
    } else
      Opts.resolver.sonatypeOssReleases.head),
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  sonatypeProfileName := organization.value,
  homepage := Some(url("https://rstradling/awsio/")),
  scmInfo := Some(ScmInfo(url("https://github.com/rstradling/awsio"), "git@github.com:rstradling/awsio.git")),
  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  developers := List(
    Developer(id="rstradling", name="Ryan J Stradling", email="ryanstradling@gmail.com", url=url("https://www.github.com/rstradling"))),
  // For evicting Scoverage out of the generated POM
  // See: https://github.com/scoverage/sbt-scoverage/issues/153
  pomPostProcess := { (node: xml.Node) =>
    new RuleTransformer(new RewriteRule {
      override def transform(node: xml.Node): Seq[xml.Node] = node match {
        case e: Elem
            if e.label == "dependency" && e.child.exists(child =>
              child.label == "groupId" && child.text == "org.scoverage") =>
          Nil
        case _ => Seq(node)
      }
    }).transform(node).head
  },
)

lazy val root = (project in file("."))
  .settings(
    moduleName := "aws-io-root",
    name := "awsio-io-root",
    publishArtifact := false
  )
  .aggregate(s3, sns, sqs, util, sqsFs2, examples)
  .dependsOn(s3, sns, sqs, util, sqsFs2, examples)
  .settings(sharedSettings)

lazy val s3 = project
  .settings(
    moduleName := "awsio-s3",
    name := "awsio-s3",
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "s3" % awsVersion 
    )
  )
  .settings(sharedSettings)

lazy val sns = project
  .settings(
    moduleName := "awsio-sns",
    name := "awsio-sns",
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "sns" % awsVersion 
    )
  )
  .settings(sharedSettings)

lazy val sqs = project
  .settings(
    moduleName := "awsio-sqs",
    name := "awsio-sqs",
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "sqs" % awsVersion 
    )
  )
  .settings(sharedSettings)

lazy val sqsFs2 = (project in file("sqs-fs2"))
  .settings(
    moduleName := "awsio-sqs-fs2",
    name := "awsio-sqs-fs2",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % fs2Version 
    )
  )
  .dependsOn(sqs)
  .settings(sharedSettings)

lazy val util = project
  .settings(
    moduleName := "awsio-util",
    name := "awsio-util",
  )
  .settings(sharedSettings)

lazy val examples = project
  .dependsOn(s3, sns, sqs, util, sqsFs2)
  .settings(sharedSettings)
