
val scalaSettings = Seq(
  scalaVersion := "2.12.10",
  scalacOptions ++= compilerOptions
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xlint",
  "-language:_",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

lazy val dependencies = Seq(
  "org.typelevel" %% "cats-effect" % "2.1.3",
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-mtl-core" % "0.7.1",
  "com.twitter" %% "finagle-http" % "20.5.0",
  "com.tethys-json" %% "tethys" % "0.11.0",
  "org.flywaydb" % "flyway-core" % "6.4.4",
  "org.postgresql" % "postgresql" % "42.2.13",
  "org.apache.commons" % "commons-dbcp2" % "2.7.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "me.scf37.config3" %% "config3" % "1.0.4"
)

// Create a test Scala style task to run with tests
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := scalastyle.in(Test).toTask("").value
(test in Test) := ((test in Test) dependsOn testScalastyle).value
(scalastyleConfig in Test) := baseDirectory.value/"scalastyle-test-config.xml"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(test in Test) := ((test in Test) dependsOn compileScalastyle).value

scalastyleFailOnError := true
scalastyleFailOnWarning := true
(scalastyleFailOnError in Test) := true
(scalastyleFailOnWarning in Test) := true

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.1.2",
  "ru.yandex.qatools.embed" % "postgresql-embedded" % "2.10"
).map(_ % "test")

val functionalTodoApp = project.in(file("."))
  .settings(scalaSettings)
  .enablePlugins(PackPlugin)
  .settings(packMain := Map("functionaltodoapp" -> "com.dylanm.functionalTodoApp.Main"))
  .settings(resolvers += "Scf37" at "https://dl.bintray.com/scf37/maven/")
  .settings(libraryDependencies ++= dependencies)
  .settings(libraryDependencies ++= testDependencies)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

