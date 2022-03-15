import Dependencies._
import sbt._

organization in ThisBuild := "com.twosixlabs.dart.nlp"
name := "dart-odinson"
scalaVersion in ThisBuild := "2.12.7"

resolvers in ThisBuild ++= Seq( "Maven Central" at "https://repo1.maven.org/maven2/",
                                ( "Clulab Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release" ).withAllowInsecureProtocol( true ),
                                "Local Ivy Repository" at s"file://${System.getProperty( "user.home" )}/.ivy2/local/default" )

publishMavenStyle := true

lazy val root = ( project in file( "." ) ).settings( libraryDependencies ++= dartCommons
                                                                             ++ jacksonOverride
                                                                             ++ betterFiles
                                                                             ++ scalaTest
                                                                             ++ clulab
                                                                             ++ odinson
                                                                             ++ taxero,
                                                     dependencyOverrides ++= jacksonOverride,
                                                     excludeDependencies ++= Seq( ExclusionRule( "org.slf4j", "slf4j-log4j12" ),
                                                                                  ExclusionRule( "org.slf4j", "log4j-over-slf4j" ),
                                                                                  ExclusionRule( "log4j", "log4j" ),
                                                                                  ExclusionRule( "org.apache.logging.log4j", "log4j-core" ) ) )

test in publish := {}

javacOptions in ThisBuild ++= Seq( "-source", "8", "-target", "8" )
scalacOptions in ThisBuild += "-target:jvm-1.8"
