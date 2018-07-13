import Dependencies._
import scala.xml.Elem
import scala.xml.transform.{RewriteRule, RuleTransformer}

organization in ThisBuild := "com.github.rstradling"

val sharedSettings = Seq(
  organization := "com.github.rstradling",
  crossScalaVersions := Seq("2.11.12", "2.12.6"),
  scalacOptions ++= Seq(
    /*"-Xfatal-warnings",*/
    "-Ywarn-unused-import",
    "-Ypartial-unification",
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-target:jvm-1.8",
    "-unchecked",
    "-Ywarn-value-discard",
    "-Ywarn-numeric-widen"
  ),
  version := "0.1.0-SNAPSHOT",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.1.0",
    "org.typelevel" %% "cats-effect" % "1.0.0-RC2",
    scalaTest % Test
  ),
  isSnapshot := version.value endsWith "SNAPSHOT",
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging),
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  sonatypeProfileName := organization.value,
  pomExtra :=
    <developers>
      <developer>
        <id>rstradling</id>
        <name>Ryan Stradling</name>
        <url>https://github/rstradling</url>
      </developer>
      <developer>
        <id>dustinfarist</id>
        <name>Dustin Farist</name>
        <url>https://github.com/dustinfarist</url>
      </developer>
    </developers>,
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

lazy val root = project
  .settings(
    moduleName := "aws-io-root",
    name := "awsio-io-root"
  )
  .aggregate(s3, sns, sqs, util, sqsFs2, sqsMonix, examples)
  .dependsOn(s3, sns, sqs, util, sqsFs2, sqsMonix)

lazy val s3 = project
  .settings(
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "s3" % "2.0.0-preview-10"
    )
  )
  .settings(sharedSettings)

lazy val sns = project
  .settings(
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "sns" % "2.0.0-preview-10"
    )
  )
  .settings(sharedSettings)

lazy val sqs = project
  .settings(
    libraryDependencies ++= Seq(
      "software.amazon.awssdk" % "sqs" % "2.0.0-preview-10"
    )
  )
  .settings(sharedSettings)

lazy val sqsFs2 = (project in file("sqs-fs2"))
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % "1.0.0-M1"
    )
  )
  .dependsOn(sqs)

lazy val sqsMonix = (project in file("sqs-monix"))
  .settings(
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % "3.0.0-RC1"
    )
  )
  .dependsOn(sqs)

lazy val util = project
  .settings(
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % "3.0.0-RC1"
    )
  )
  .settings(sharedSettings)

lazy val examples = project
  .dependsOn(s3, sns, sqs, util, sqsFs2, sqsMonix)
  .settings(sharedSettings)
