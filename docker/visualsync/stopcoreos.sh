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


INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
date_echo "stopping console@${INSTANCE} from host $(uname -n)"
do_shell fleetctl --version
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-machines
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop console@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop console-discovery@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false stop empty-mongodb@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload console-discovery@${INSTANCE}.service

#remove all traces of service files on cluster - needed in case the checked in service file has changed
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false destroy console@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false destroy console@.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false destroy empty-mongodb@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false destroy empty-mongodb@.service

#fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload console@${INSTANCE}.service
#fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false unload empty-mongodb@${INSTANCE}.service
do_shell fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-units
date_echo "console should be dead now"

