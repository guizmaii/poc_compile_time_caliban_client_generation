import sbt.librarymanagement.Resolver

resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.github.sbt"            % "sbt-native-packager" % "1.9.9")
addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix"        % "0.10.1")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("pl.project13.scala"        % "sbt-jmh"             % "0.4.3")
addSbtPlugin("com.timushev.sbt"          % "sbt-updates"         % "0.6.3")
addSbtPlugin("com.eed3si9n"              % "sbt-buildinfo"       % "0.11.0")
addSbtPlugin("ch.epfl.scala"             % "sbt-bloop"           % "1.5.2")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"       % "2.0.6")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"        % "0.4.1")
addSbtPlugin("io.spray"                  % "sbt-revolver"        % "0.9.1")
addSbtPlugin("com.github.ghostdogpr"     % "caliban-codegen-sbt" % "1.4.3")
addSbtPlugin("com.guizmaii"              % "sbt-datadog"         % "4.4.0")
