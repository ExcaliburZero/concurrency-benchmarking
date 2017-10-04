name := "concurrencybench"

version := "1.0"

scalaVersion := "2.12.1"
sbtVersion := "0.13.13"

resolvers += Opts.resolver.sonatypeSnapshots

mainClass in Compile := Some("concurrencybench.Main")

// Linting
resolvers += Resolver.sonatypeRepo("snapshots")
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT")
