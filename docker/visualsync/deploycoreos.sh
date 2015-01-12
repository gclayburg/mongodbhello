#!/bin/bash

date
INSTANCE=${INSTANCE:-9}
fleetctl --version
fleetctl -tunnel mink -strict-host-key-checking=false list-machines
fleetctl -tunnel mink -strict-host-key-checking=false stop console@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false stop console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false stop empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false unload console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
echo "console should be dead now"
date
#sleep 30
fleetctl -tunnel mink -strict-host-key-checking=false start console@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
date
   echo "checking key: /services/website/console@${INSTANCE}..."
if url=$(etcdctl -C=http://mink:4001 get /services/website/console@${INSTANCE}); then
  echo ok
else
   echo "waiting for key: /services/website/console@${INSTANCE}..."
   url=$(etcdctl -C=http://mink:4001 watch /services/website/console@${INSTANCE})
fi
date
myurl=$(echo $url | jq -r '.url')
echo "url is: $myurl"
ENDPOINT="$(echo $url | jq -r '.host'):$(echo $url | jq -r '.port')"
echo "endpoint is: $ENDPOINT"
#create properties file on descriptor #3,
# stdout contains log output of all commands in this script
# stderr contains normal errors
>&3 echo "ENDPOINT=$ENDPOINT"
isTomcatRunning.sh $myurl

