#!/bin/bash

date_echo(){
    datestamp=$(date +%F_%T)
    echo "${datestamp} $*"
}

do_shell(){
# Execute command in shell, while logging complete command to stdout
    echo "$(date +%F_%T) --> $*"
    eval "$*"
    STATUS=$?
    return $STATUS
}

date
INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
date_echo "stopping console@${INSTANCE}"
do_shell fleetctl --version
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-machines
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop console@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop console-discovery@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop empty-mongodb@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload console-discovery@${INSTANCE}.service
#fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload console@${INSTANCE}.service
#fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload empty-mongodb@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-units
date_echo "console should be dead now"

