#!/bin/sh

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running SCG."
  exit 1
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set SCG_HOME if not already set
[ -z "$SCG_HOME" ] && SCG_HOME=`cd "$PRGDIR/.." ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$SCG_HOME" ] && SCG_HOME=`cygpath --unix "$SCG_HOME"`
  [ -n "$AXIS2_HOME" ] && TUNGSTEN_HOME=`cygpath --unix "$SCG_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# update classpath - add any patches first
SCG_CLASSPATH="$SCG_HOME/lib/patches"
for f in $SCG_HOME/lib/patches/*.jar
do
  SCG_CLASSPATH=$SCG_CLASSPATH:$f
done

SCG_CLASSPATH=$SCG_CLASSPATH:"$SCG_HOME/lib"
for f in $SCG_HOME/lib/*.jar
do
  SCG_CLASSPATH=$SCG_CLASSPATH:$f
done
SCG_CLASSPATH=$SCG_HOME/repository/conf:$JAVA_HOME/lib/tools.jar:$SCG_CLASSPATH:$CLASSPATH

# use proper bouncy castle version for the JDK
#jdk_15=`$JAVA_HOME/bin/java -version 2>&1 | grep 1.5`

#if [ "$jdk_15" ]; then
#    echo " Using Bouncy castle JAR for Java 1.5"
#    for f in $SCG_HOME/lib/bcprov-jdk15*.jar
#    do
#      SCG_CLASSPATH=$f:$SCG_CLASSPATH
#    done
#else
#    echo " [Warn] SCG is tested only with Java 5"
#fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  SCG_HOME=`cygpath --absolute --windows "$SCG_HOME"`
  AXIS2_HOME=`cygpath --absolute --windows "$SCG_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi
# endorsed dir
SCG_ENDORSED=$SCG_HOME/lib/endorsed

# SCG config
SCG_XML=$SCG_HOME/repository/conf/

# server name
SERVER_NAME=

# ----- Uncomment the following line to enalbe the SSL debug options ----------
# TEMP_PROPS="-Djavax.net.debug=all"

while [ $# -ge 1 ]; do

if [ "$1" = "-xdebug" ]; then
    XDEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,address=8000"
    shift

  elif [ "$1" = "-sample" ]; then
    SCG_XML=$SCG_HOME/repository/conf/sample/SCG_sample_$2.xml
    shift 2 # -sample and sample number

  elif [ "$1" = "-serverName" ]; then
    SERVER_NAME=$2
    shift 2 # -serverName and actual name

elif [ "$1" = "-h" ]; then
    echo "Usage: scserver.sh ( commands ... )"
    echo "commands:"
    echo "  -xdebug            Start SCG under JPDA debugger"
    echo "  -sample (number)   Start with sample SCG configuration of given number"
    echo "  -serverName <name> Name of the SCG server instance"
    shift
    exit 0

  else
    echo "Error: unknown command:$1"
    echo "For help: SCG.sh -h"
    shift
    exit 1
  fi

done

# ----- Execute The Requested Command -----------------------------------------

cd $SCG_HOME
echo "Starting SCG/Java ..."
echo "Using SCG_HOME:    $SCG_HOME"
echo "Using JAVA_HOME:       $JAVA_HOME"
echo "Using SCG_XML:     $SCG_XML"

#-Djava.io.tmpdir=$SCG_HOME/work/temp \
$JAVA_HOME/bin/java -server -Xms128M -Xmx128M \
    $XDEBUG \
    $TEMP_PROPS \
    -Djava.endorsed.dirs=$SCG_ENDORSED \
    -classpath $SCG_CLASSPATH \
    cgl.iotcloud.core.ServerManager



