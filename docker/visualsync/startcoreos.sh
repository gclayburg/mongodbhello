#!/bin/bash
RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]:-$$}" )" && pwd )"
. "${RUNDIR}"/shellbase.sh
LOG_CONTEXT="$0 -"

INSTANCE=${INSTANCE:-${1:-9}} # order of preference: env.INSTANCE, $1, 9
date_echo "starting console@${INSTANCE} from host $(uname -n)"
date_echo "using FLEETCTL_TUNNEL=${FLEETCTL_TUNNEL}"
date_echo "using FLEETCTL_ENDPOINT=${FLEETCTL_ENDPOINT}"
do_shell_fail fleetctl -strict-host-key-checking=false submit console@.service
do_shell_fail fleetctl -strict-host-key-checking=false start console@${INSTANCE}.service
date_echo "starting console-discovery@${INSTANCE}"
do_shell_fail fleetctl -strict-host-key-checking=false submit console-discovery@.service
do_shell_fail fleetctl -strict-host-key-checking=false start console-discovery@${INSTANCE}.service
date_echo "starting empty-mongodb@${INSTANCE}"
do_shell_fail fleetctl -strict-host-key-checking=false submit ../mongodb/empty-mongodb@.service
do_shell_fail fleetctl -strict-host-key-checking=false start empty-mongodb@${INSTANCE}.service
date_echo "list-units"
do_shell_fail fleetctl -strict-host-key-checking=false list-units
date_echo "console and mongodb should be starting now"
