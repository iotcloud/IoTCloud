#!/bin/sh

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running IOTCloud Samples."
  exit 1
fi

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
[ -z "$SCG_HOME" ] && SCG_HOME=`cd "$PRGDIR/../.." ; pwd`


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

SCG_CLASSPATH=$SCG_CLASSPATH:"$SCG_HOME/samples/lib"
for f in $SCG_HOME/samples/lib/*.jar
do
  SCG_CLASSPATH=$SCG_CLASSPATH:$f
done

SCG_CLASSPATH=$SCG_HOME/repository/conf:$JAVA_HOME/lib/tools.jar:$SCG_CLASSPATH:$CLASSPATH

#echo $SCG_CLASSPATH

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

elif [ "$1" = "-sensor" ]; then
    exec="cgl.iotcloud.samples.turtlebot.sensor.TurtleSensor"
    if [ "$2" = "turtle-sensor" ]; then
        exec="cgl.iotcloud.samples.turtlebot.sensor.TurtleSensor"
    elif [ "$2" = "nxt-sensor" ]; then
        exec="cgl.iotcloud.samples.lego_nxt.sensor.LegoNXTSensor"
    elif [ "$2" = "ardu-sensor" ]; then
        exec="cgl.iotcloud.samples.arducopter.sensor.ArduSensor"
    fi
    shift 2
elif [ "$1" = "-client" ]; then
    exec="cgl.iotcloud.samples.turtlebot.client.TurtleUI"
    if [ "$2" = "turtle-client" ]; then
        exec="cgl.iotcloud.samples.turtlebot.client.TurtleUI"
    elif [ "$2" = "nxt-client" ]; then
        exec="cgl.iotcloud.samples.lego_nxt.client.LegoNXTUI"
    elif [ "$2" = "ardu-client" ]; then
        exec="cgl.iotcloud.samples.arducopter.client.ArduUI"
    fi
    shift 2
elif [ "$1" = "-h" ]; then
    echo "Usage: scserver.sh ( commands ... )"
    echo "commands:"
    echo "  -xdebug            Start SCG under JPDA debugger"
    echo "  -sample (number)   Start with sample SCG configuration of given number"
    echo "  -serverName <name> Name of the SCG server instance"
    shift
    exit 0
else
    echo "Invalid options. Use -h for possible options"
    exit 0
fi

done

echo $exec

# ----- Execute The Requested Command -----------------------------------------

cd $SCG_HOME
echo "Starting SCG/Java ..."
echo "Using SCG_HOME:    $SCG_HOME"
echo "Using JAVA_HOME:       $JAVA_HOME"
echo "Using SCG_XML:     $SCG_XML"
#echo "Using SCG_CLASSPATH: $SCG_CLASSPATH"

#-Djava.io.tmpdir=$SCG_HOME/work/temp \
$JAVA_HOME/bin/java -server -Xms128M -Xmx128M \
    $XDEBUG \
    $TEMP_PROPS \
    -Djava.endorsed.dirs=$SCG_ENDORSED \
    -classpath $SCG_CLASSPATH \
    $exec test.c



