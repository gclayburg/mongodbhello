#!/bin/bash

date
INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
echo "starting instance $INSTANCE"
fleetctl -tunnel mink -strict-host-key-checking=false start console@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units

