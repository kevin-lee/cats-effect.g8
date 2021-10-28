import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.SystemV

ThisBuild / scalaVersion := props.ScalaVersion
ThisBuild / version := props.ProjectVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := props.OrgName
ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "$author_name$",
    "$author_email$",
    url(s"https://github.com/\${props.GitHubUsername}"),
  )
)
ThisBuild / homepage := url(s"https://github.com/\${props.GitHubUsername}/\${props.RepoName}").some
ThisBuild / scmInfo :=
  ScmInfo(
    url(s"https://github.com/\${props.GitHubUsername}/\${props.RepoName}"),
    s"https://github.com/\${props.GitHubUsername}/\${props.RepoName}.git",
  ).some

lazy val root = (project in file("."))
  .settings(
    name := props.ProjectName
  )
  .settings(noPublish)
  .aggregate(core, app)

lazy val core = subProject("core", file("core"))
  .settings(
    libraryDependencies ++= List(libs.newtype) ++ libs.refined ++ libs.catsAndCatsEffect
  )

lazy val app = subProject("app", file("app"))
  .enablePlugins(JavaAppPackaging)
  .settings(debianPackageInfo)
  .settings(
    maintainer := "$author_name$ <$author_email$>"
  )
  .dependsOn(
    core % props.IncludeTest
  )

lazy val props =
  new {
    val ScalaVersion = "$scalaVersion$"
    val Org          = "$organization$"
    val OrgName      = "$organizationName$"

    val GitHubUsername = "$github_username$"
    val RepoName       = "$repo_name$"
    val ProjectName    = "$project_name$"
    val ProjectVersion = "0.1.0-SNAPSHOT"

    val newtypeVersion = "$newtype_version$"
    val refinedVersion = "$refined_version$"

    val hedgehogVersion = "$hedgehog_version$"

    val catsVersion       = "$cats_version$"
    val catsEffectVersion = "$cats_effect_version$"

    val IncludeTest: String = "compile->compile;test->test"
  }

lazy val libs =
  new {
    lazy val hedgehogLibs = List(
      "qa.hedgehog" %% "hedgehog-core"   % props.hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-runner" % props.hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-sbt"    % props.hedgehogVersion % Test,
    )

    lazy val newtype = "io.estatico" %% "newtype" % props.newtypeVersion

    lazy val refined = List(
      "eu.timepit" %% "refined"      % props.refinedVersion,
      "eu.timepit" %% "refined-cats" % props.refinedVersion,
    )

    lazy val catsAndCatsEffect = List(
      "org.typelevel" %% "cats-core"   % props.catsVersion,
      "org.typelevel" %% "cats-effect" % props.catsEffectVersion,
    )

  }

// format: off
def prefixedProjectName(name: String) = s"\${props.RepoName}\${if (name.isEmpty) "" else s"-\$name"}"
// format: on

def subProject(projectName: String, file: File): Project =
  Project(projectName, file)
    .settings(
      name := prefixedProjectName(projectName),
      addCompilerPlugin("org.typelevel" % "kind-projector"     % "0.13.2" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1"),
      libraryDependencies ++= libs.hedgehogLibs,
      testFrameworks ~= (testFws => (TestFramework("hedgehog.sbt.Framework") +: testFws).distinct),
    )

lazy val debianPackageInfo: SettingsDefinition = List(
  Linux / maintainer := "$author_name$ <$author_email$>",
  Linux / packageSummary := "My App",
  packageDescription := "My app is ...",
  Debian / serverLoading := SystemV.some,
)

lazy val noPublish: SettingsDefinition = List(
  publish := {},
  publishM2 := {},
  publishLocal := {},
  publishArtifact := false,
  sbt.Keys.`package` / skip := true,
  packagedArtifacts / skip := true,
  publish / skip := true,
)
