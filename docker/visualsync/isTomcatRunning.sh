#!/bin/bash
RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]:-$$}" )" && pwd )"
. "${RUNDIR}"/shellbase.sh
LOG_CONTEXT="$0 -"

URL=$1
date_echo "checking for running tomcat at: $URL"
if [ -x /usr/bin/curl ]; then
  typeset -i max_seconds=180
  typeset -i counter=0
  until [[ "`curl --silent --show-error --connect-timeout 1 -I $URL 2> /dev/null | grep -E 'Coyote|Jetty'`" != "" || ($counter == $max_seconds ) ]];
  do
    date_echo "waiting for tomcat to respond"
    sleep 1
    counter=$((counter +1))
  done
    date_echo "Tomcat is ready!"
else
  date_echo "waiting for tomcat to respond"
  do_shell "sleep 120"
  date_echo "Tomcat is most likely ready"
fi


