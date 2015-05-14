#!/bin/bash
RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]:-$$}" )" && pwd )"
echo "RUNDIR: $RUNDIR"
. "${RUNDIR}"/shellbase.sh
LOG_CONTEXT="$0 -"
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
echo "ENDPOINT=$ENDPOINT" > chosenone.properties

#tomcat usually takes longer to startup.  mongodb may be slow if mongodb docker image has not yet been installed on this host.  both need to be running before clients can use the service
if "${RUNDIR}"/isTomcatRunning.sh $myurl ; then
  if "${RUNDIR}"/isMongoRunning.sh ${myurl/:8*}:27017 ; then  # for now, we assume that mongodb service needed will always be running on port 27017 on the same host as tomcat
    date_echo "tomcat and mongodb should be running"
    exit 0
  fi
fi
exit 1
