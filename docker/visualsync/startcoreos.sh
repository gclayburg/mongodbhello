#!/bin/bash

date
INSTANCE=${INSTANCE:-9}
fleetctl -tunnel mink -strict-host-key-checking=false start console@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
date
echo "checking key: /services/website/console@${INSTANCE}..."
if url=$(etcdctl -C=http://mink:4001 get /services/website/console@${INSTANCE} 2> /dev/null); then
  echo ok
  myurl=$(echo $url | jq -r '.url')
else
  echo "waiting for key: /services/website/console@${INSTANCE}..."
  if url=$(etcdctl -C=http://mink:4001 watch /services/website/console@${INSTANCE}); then
    myurl=$(echo $url | jq -r '.url')
  else
    exit 500
  fi
fi
date
echo "tomcat url is: $myurl"
ENDPOINT="$(echo $url | jq -r '.host'):$(echo $url | jq -r '.port')"
echo "endpoint is: $ENDPOINT"
echo "ENDPOINT=$ENDPOINT" > thechosenone.properties

isTomcatRunning.sh $myurl
