#!/bin/bash

date
INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
echo "stopping console@${INSTANCE}"
fleetctl --version
fleetctl -tunnel mink -strict-host-key-checking=false list-machines
fleetctl -tunnel mink -strict-host-key-checking=false stop console@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false stop console-discovery@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false stop empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false unload console-discovery@${INSTANCE}.service
#fleetctl -tunnel mink -strict-host-key-checking=false unload console@${INSTANCE}.service
#fleetctl -tunnel mink -strict-host-key-checking=false unload empty-mongodb@${INSTANCE}.service
fleetctl -tunnel mink -strict-host-key-checking=false list-units
echo "console should be dead now"
date
