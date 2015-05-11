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
date_echo "starting console instance $INSTANCE"
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start console@${INSTANCE}.service
date_echo "starting console-discovery instance $INSTANCE"
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
date_echo "starting empty-mongodb instance $INSTANCE"
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
date_echo "list-units"
fleetctl -tunnel mink --endpoint http://192.168.1.58:4001 -strict-host-key-checking=false list-units

