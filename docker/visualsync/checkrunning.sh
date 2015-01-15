#!/bin/bash

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
echo "ENDPOINT=$ENDPOINT" > /tmp/thechosenone.properties

isTomcatRunning.sh $myurl
