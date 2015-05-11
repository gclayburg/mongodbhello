#!/bin/bash

date
INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
echo "starting instance $INSTANCE"
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start console@${INSTANCE}.service
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-units

