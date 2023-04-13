package dist

case class BashScript(
  mainClass: String,
  jvmOpts: List[JvmOpt] = List.empty,
  jarsDirectory: String = "/lib/jar",
  relativeScriptDepth: Int = 1,
) {
  lazy val fullScript =
      headerScript +
      mainEnvScript +
      resolveJvmScript +
      cygwinPathScript +
      systemPropertiesScript +
      executeScript

  lazy val headerScript =
    """|#!/bin/bash
       |BASH_START_VERBOSE=0
       |# resolve links - $0 may be a softlink
       |PRG="$0"
       |
       |while [ -h "$PRG" ]; do
       |  ls=`ls -ld "$PRG"`
       |  link=`expr "$ls" : '.*-> \(.*\)$'`
       |  if expr "$link" : '/.*' > /dev/null; then
       |    PRG="$link"
       |  else
       |    PRG=`dirname "$PRG"`/"$link"
       |  fi
       |done
       |""".stripMargin

  def relativeToAppHome:String =
    relativeScriptDepth match {
      case _ if relativeScriptDepth <= 0 => ""
      case 1 => "/.."
      case 2 => "/../.."
      case _ => "/.." * relativeScriptDepth
    }

  lazy val mainEnvScript =
   s"""|PRGDIR=`dirname "$$PRG"`
       |BASEDIR=`cd "$$PRGDIR$relativeToAppHome" >/dev/null; pwd`
       |[ "$$BASH_START_VERBOSE" == "1" ] && echo BASEDIR=$$BASEDIR
       |
       |CLASSPATH="$$BASEDIR$jarsDirectory/*"
       |[ "$$BASH_START_VERBOSE" == "1" ] && echo CLASSPATH=$$CLASSPATH
       |
       |APP_NAME=$$(basename $$0)
       |[ "$$BASH_START_VERBOSE" == "1" ] && echo APP_NAME=$$APP_NAME
       |
       |MAINCLASS=$mainClass
       |[ "$$BASH_START_VERBOSE" == "1" ] && echo MAINCLASS=$$MAINCLASS
       |""".stripMargin

  // lazy val resetRepo = 
  //   """|# Reset the REPO variable. If you need to influence this use the environment setup file.
  //      |REPO=
  //      |""".stripMargin

  lazy val resolveJvmScript = 
    """|# OS specific support.  $var _must_ be set to either true or false.
       |cygwin=false;
       |darwin=false;
       |case "`uname`" in
       |  CYGWIN*) cygwin=true ;;
       |  Darwin*) darwin=true
       |           if [ -z "$JAVA_VERSION" ] ; then
       |             JAVA_VERSION="CurrentJDK"
       |           else
       |             echo "Using Java version: $JAVA_VERSION"
       |           fi
       |		   if [ -z "$JAVA_HOME" ]; then
       |		      if [ -x "/usr/libexec/java_home" ]; then
       |			      JAVA_HOME=`/usr/libexec/java_home`
       |			  else
       |			      JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
       |			  fi
       |           fi       
       |           ;;
       |esac
       |
       |if [ -z "$JAVA_HOME" ] ; then
       |  if [ -r /etc/gentoo-release ] ; then
       |    JAVA_HOME=`java-config --jre-home`
       |  fi
       |fi
       |
       |# For Cygwin, ensure paths are in UNIX format before anything is touched
       |if $cygwin ; then
       |  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
       |  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
       |fi
       |
       |# If a specific java binary isn't specified search for the standard 'java' binary
       |if [ -z "$JAVACMD" ] ; then
       |  if [ -n "$JAVA_HOME"  ] ; then
       |    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
       |      # IBM's JDK on AIX uses strange locations for the executables
       |      JAVACMD="$JAVA_HOME/jre/sh/java"
       |    else
       |      JAVACMD="$JAVA_HOME/bin/java"
       |    fi
       |  else
       |    JAVACMD=`which java`
       |  fi
       |fi
       |
       |if [ ! -x "$JAVACMD" ] ; then
       |  echo "Error: JAVA_HOME is not defined correctly." 1>&2
       |  echo "  We cannot execute $JAVACMD" 1>&2
       |  exit 1
       |fi
       |
       |""".stripMargin

  lazy val cygwinPathScript =
    """|
       |# For Cygwin, switch paths to Windows format before running java
       |if $cygwin; then
       |  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
       |  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
       |  [ -n "$HOME" ] && HOME=`cygpath --path --windows "$HOME"`
       |  [ -n "$BASEDIR" ] && BASEDIR=`cygpath --path --windows "$BASEDIR"`
       |fi
       |
       |""".stripMargin

  lazy val systemPropertiesScript =
    jvmOpts.map { prop => 
      s"""|if [ -z "$${JAVA_OPTS+x}" ] ; then
          |  JAVA_OPTS="${prop.cmdLine}"
          |else
          |  JAVA_OPTS="$$JAVA_OPTS ${prop.cmdLine}"
          |fi
          |""".stripMargin
    }.mkString("\n")

  lazy val executeScript = 
    """|exec "$JAVACMD" $JAVA_OPTS  \
       |  -classpath "$CLASSPATH" \
       |  -Dapp.name="$APP_NAME" \
       |  -Dapp.pid="$$" \
       |  -Dapp.home="$BASEDIR" \
       |  -Dbasedir="$BASEDIR" \
       |  $MAINCLASS \
       |  "$@"
       |""".stripMargin
}