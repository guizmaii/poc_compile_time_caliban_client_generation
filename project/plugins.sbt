import sbt.librarymanagement.Resolver

resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.github.sbt"            % "sbt-native-packager" % "1.9.7")
addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix"        % "0.9.33")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("pl.project13.scala"        % "sbt-jmh"             % "0.4.3")
addSbtPlugin("com.timushev.sbt"          % "sbt-updates"         % "0.6.1")
addSbtPlugin("com.eed3si9n"              % "sbt-buildinfo"       % "0.10.0")
addSbtPlugin("ch.epfl.scala"             % "sbt-bloop"           % "1.4.11")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"       % "1.9.2")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"        % "0.1.20")
addSbtPlugin("com.github.ghostdogpr"     % "caliban-codegen-sbt" % "1.3.1")
