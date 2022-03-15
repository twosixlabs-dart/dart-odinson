import sbt._

object Dependencies {

    val slf4jVersion = "1.7.36"
    val logbackVersion = "1.2.10"
    val scalaTestVersion = "3.0.5"
    val betterFilesVersion = "3.8.0"

    val dartCommonsVersion = "3.0.310"

    val odinsonVersion = "0.5.0"
    val clulabVersion = "8.2.3"
    val taxeroVersion = "0.0.1.dart-01"

    val jacksonOverrideVersion = "2.13.1"

    val jacksonOverride = Seq( "com.fasterxml.jackson.module" % "jackson-module-paranamer" % jacksonOverrideVersion,
                               "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonOverrideVersion,
                               "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonOverrideVersion,
                               "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonOverrideVersion )

    val odinson = Seq( "ai.lum" %% "odinson-core" % odinsonVersion )

    val clulab = Seq( "org.clulab" %% "processors-main" % clulabVersion,
                      "org.clulab" %% "processors-corenlp" % clulabVersion )

    val taxero = Seq( "org.clulab" %% "taxero" % taxeroVersion )

    val logging = Seq( "org.slf4j" % "slf4j-api" % slf4jVersion,
                       "ch.qos.logback" % "logback-classic" % logbackVersion )

    val scalaTest = Seq( "org.scalatest" %% "scalatest" % scalaTestVersion % Test )
    val betterFiles = Seq( "com.github.pathikrit" %% "better-files" % betterFilesVersion )

    val dartCommons = Seq( "com.twosixlabs.dart" %% "dart-json" % dartCommonsVersion,
                           "com.twosixlabs.dart" %% "dart-test-base" % dartCommonsVersion % Test,
                           "com.twosixlabs.dart" %% "dart-utils" % dartCommonsVersion )


}
