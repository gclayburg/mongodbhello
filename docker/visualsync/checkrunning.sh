#!/bin/bash
RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]:-$$}" )" && pwd )"
echo "RUNDIR: $RUNDIR"
. "${RUNDIR}"/shellbase.sh
LOG_CONTEXT="$0 *"
INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
date_echo "checking key: /services/website/console@${INSTANCE}..."
if url=$(etcdctl -C=http://mink:4001 get /services/website/console@${INSTANCE} 2> /dev/null); then
  echo ok
  myurl=$(echo $url | jq -r '.url')
else
  date_echo "waiting for key: /services/website/console@${INSTANCE}..."
  if url=$(etcdctl -C=http://mink:4001 watch /services/website/console@${INSTANCE}); then
    myurl=$(echo $url | jq -r '.url')
  else
    exit 500
  fi
fi
date_echo "tomcat url is: $myurl"
ENDPOINT="$(echo $url | jq -r '.host'):$(echo $url | jq -r '.port')"
date_echo "endpoint is: $ENDPOINT"
date_echo "ENDPOINT=$ENDPOINT" > chosenone.properties

isTomcatRunning.sh $myurl
date_echo "tomcat should be up"