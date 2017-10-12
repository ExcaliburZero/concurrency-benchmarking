name := "concurrencybench"

version := "1.0"

scalaVersion := "2.12.1"
//scalaVersion := "2.11.8"
sbtVersion := "0.13.13"

resolvers += Opts.resolver.sonatypeSnapshots

mainClass in Compile := Some("concurrencybench.Main")

//libraryDependencies += "com.typesafe.akka" %% "akka-actor.10" % "2.1.3"

// Linting
resolvers += Resolver.sonatypeRepo("snapshots")
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT")

// Benchmarking
enablePlugins(JmhPlugin)
