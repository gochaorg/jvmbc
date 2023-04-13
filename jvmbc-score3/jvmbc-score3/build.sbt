import _root_.dist.JvmOpt
import java.nio.charset.StandardCharsets
import dist._

// The simplest possible sbt build file is just one line:

scalaVersion := "3.2.2"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.8"

// It's possible to define many kinds of settings, such as:

name := "jvmbc-score3"
organization := "xyz.cofe"
version := "1.0"

scalacOptions ++= Seq(
  "-Xmax-inlines:128"
)


// Note, it's not required for you to define these three settings. These are
// mostly only necessary if you intend to publish your library's binaries on a
// place like Sonatype.


// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"
libraryDependencies += "org.ow2.asm" % "asm" % "9.2" withSources() withJavadoc()
libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

//libraryDependencies += "xyz.cofe" %% "json4s3" % "0.0.2" from "file:/Users/g.kamnev/code/my-b/json4s3/target/scala-3.2.0/json4s3_3-0.0.2.jar"
libraryDependencies += "xyz.cofe" %% "json4s3" % "2.1.0"
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

// Here, `libraryDependencies` is a set of dependencies, and by using `+=`,
// we're adding the scala-parser-combinators dependency to the set of dependencies
// that sbt will go and fetch when it starts up.
// Now, in any Scala file, you can import classes, objects, etc., from
// scala-parser-combinators with a regular import.

// TIP: To find the "dependency" that you need to add to the
// `libraryDependencies` set, which in the above example looks like this:

// "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"

// You can use Scaladex, an index of all known published Scala libraries. There,
// after you find the library you want, you can just copy/paste the dependency
// information that you need into your build file. For example, on the
// scala/scala-parser-combinators Scaladex page,
// https://index.scala-lang.org/scala/scala-parser-combinators, you can copy/paste
// the sbt dependency from the sbt box on the right-hand side of the screen.

// IMPORTANT NOTE: while build files look _kind of_ like regular Scala, it's
// important to note that syntax in *.sbt files doesn't always behave like
// regular Scala. For example, notice in this build file that it's not required
// to put our settings into an enclosing object or class. Always remember that
// sbt is a bit different, semantically, than vanilla Scala.

// ============================================================================

// Most moderately interesting Scala projects don't make use of the very simple
// build file style (called "bare style") used in this build.sbt file. Most
// intermediate Scala projects make use of so-called "multi-project" builds. A
// multi-project build makes it possible to have different folders which sbt can
// be configured differently for. That is, you may wish to have different
// dependencies or different testing frameworks defined for different parts of
// your codebase. Multi-project builds make this possible.

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "ch.epfl.scala",
//       scalaVersion := "2.13.8"
//     )),
//     name := "hello-world"
//   )

// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html

val distDir = settingKey[File]("Distributive directory")
distDir :=  baseDirectory.value / "dist"

val distJarsDirectory             = settingKey[File]("Where to copy all libs and built artifact")
distJarsDirectory                := distDir.value / "lib" / "jar"

val copyAllLibsAndArtifact  = taskKey[Unit]("Copy runtime dependencies and built artifact to 'distJarsDirectory'")
copyAllLibsAndArtifact   := {
  //val allLibs:                List[File]          = dependencyClasspath.in(Runtime).value.map(_.data).filter(_.isFile).toList
  val allLibs:                List[File]          = ( Runtime / dependencyClasspath ).value.map(_.data).filter(_.isFile).toList
  //val buildArtifact:          File                = packageBin.in(Runtime).value
  val buildArtifact:          File                = ( Runtime / packageBin ).value
  val jars:                   List[File]          = buildArtifact :: allLibs
  val `mappings src->dest`:   List[(File, File)]  = jars.map(f => (f, distJarsDirectory.value / f.getName))
  val log                                         = streams.value.log
  log.info(s"Copying to ${distJarsDirectory.value}:")
  log.info(s"  ${`mappings src->dest`.map(_._1).mkString("\n")}")
  IO.copy(`mappings src->dest`)
}

val distBinDir = taskKey[File]("Prepare dist / bin")
distBinDir := {
  val dist = distDir.value
  val bin = new File(dist, "bin")
  if( bin.exists ){
    bin.mkdirs()
  }
  bin
}

val mainClass = "xyz.cofe.jtfm.Main"

val jvmOpts = List[JvmOpt](
  JvmOpt.Custom("-Dxyz.cofe.term.default=auto")
)
val winJvmOpts = JvmOpt.Custom("-Djtfm.console=win") :: jvmOpts
val nixJvmOpts = JvmOpt.Custom("-Djtfm.console=nix") :: jvmOpts

val binBashScriptSrc  = BashScript (mainClass,jvmOpts=nixJvmOpts).fullScript
val binBatchScriptSrc = BatchScript(mainClass,jvmOpts=winJvmOpts, javaExe=JavaExe.window).fullScript

val bashScript = taskKey[Unit]("Generate bash launch script")
bashScript := {
  val file = new File(distBinDir.value, "jtfm.sh")
  IO.write(file, binBashScriptSrc.getBytes("UTF-8") )
  file.setExecutable(true)
}

val batScript = taskKey[Unit]("Generate batch launch script")
batScript := {
  val file = new File(distBinDir.value, "jtfm.bat")
  IO.write(file, binBatchScriptSrc.getBytes(StandardCharsets.ISO_8859_1) )
  file.setExecutable(true)
}

val dist = taskKey[Unit]("Generate dist")
dist := {
  batScript.value
  bashScript.value
  copyAllLibsAndArtifact.value
}


val distClean = taskKey[Unit]("clean dist")
distClean := {
  val distDir0 = distDir.value
  IO.delete(distDir0)
}