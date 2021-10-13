import sbt.librarymanagement.Resolver

resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.github.sbt"            % "sbt-native-packager" % "1.9.5")
addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix"        % "0.9.31")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.4.3")
addSbtPlugin("pl.project13.scala"        % "sbt-jmh"             % "0.4.3")
addSbtPlugin("com.timushev.sbt"          % "sbt-updates"         % "0.6.0")
addSbtPlugin("com.eed3si9n"              % "sbt-buildinfo"       % "0.10.0")
addSbtPlugin("ch.epfl.scala"             % "sbt-bloop"           % "1.4.9")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"       % "1.9.0")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"        % "0.1.20")
addSbtPlugin("com.github.ghostdogpr"     % "caliban-codegen-sbt" % "1.2.0-SNAPSHOT-304")
