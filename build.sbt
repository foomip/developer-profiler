name := """developer-profiler"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.xerial"                %  "sqlite-jdbc"                % "3.8.10.1",
  "com.typesafe.play"         %% "play-slick"                 % "1.0.0",
  "com.typesafe.play"         %% "play-slick-evolutions"      % "1.0.0",
  "com.sksamuel.elastic4s"    %% "elastic4s-core"             % "1.6.2",
  "net.sourceforge.htmlunit"  %  "htmlunit"                   % "2.17",
  cache,
  ws,
  specs2 % Test
)

scalacOptions += "-feature"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
