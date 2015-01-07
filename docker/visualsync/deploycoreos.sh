#!/bin/bash

date
fleetctl --version
fleetctl -tunnel mink -strict-host-key-checking=false list-machines
fleetctl -tunnel mink -strict-host-key-checking=false stop console@9.service
fleetctl -tunnel mink -strict-host-key-checking=false stop console-discovery@9.service
fleetctl -tunnel mink -strict-host-key-checking=false stop empty-mongodb@9.service
fleetctl -tunnel mink -strict-host-key-checking=false unload console-discovery@9.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
echo "console should be dead now"
date
#sleep 30
fleetctl -tunnel mink -strict-host-key-checking=false start console@9.service
fleetctl -tunnel mink -strict-host-key-checking=false start console-discovery@9.service
fleetctl -tunnel mink -strict-host-key-checking=false start empty-mongodb@9.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
if url=$(etcdctl -C=http://mink:4001 get /services/website/console@9); then
  echo ok
else
   echo "wait for it..."
   url=$(etcdctl -C=http://mink:4001 watch /services/website/console@9)
fi

myurl=$(echo $url | jq -r '.url')
echo "url is: $myurl"
isTomcatRunning.sh $myurl

