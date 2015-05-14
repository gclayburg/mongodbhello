#!/bin/bash
RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]:-$$}" )" && pwd )"
. "${RUNDIR}"/shellbase.sh
LOG_CONTEXT="$0 -"

URL=$1
declare -i WAIT_SECONDS=${2:-120} #120 seconds default wait time
declare -i TRIES=0
date_echo "checking for running mongodb at: $URL"
if [ -x /usr/bin/curl ]; then
  curl --silent --connect-timeout 1 -I $URL
  STATUS=$?
  # curl exit code 52: tcp connect worked, but protocol mismatch
  until (( STATUS == 52 || (TRIES++ >= WAIT_SECONDS ) )); do
    date_echo "waiting for mongodb to come online $TRIES"
    sleep 1
    curl --silent --connect-timeout 1 -I $URL
    STATUS=$?
  done
  if (( STATUS == 52 )); then
    date_echo "mongodb is ready!"
    exit 0
  else
    date_echo "Timeout waiting for mongodb to come online: $WAIT_SECONDS seconds."
    exit 1
  fi
else
  date_echo "waiting for mongodb to respond"
  do_shell "sleep 120"
  date_echo "mongodb is most likely ready.  This is just a guess, though.  curl is not installed."
  exit 0
fi


