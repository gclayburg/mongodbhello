#!/bin/sh
date_echo(){
    datestamp=$(date)
    echo "${datestamp} $*"
}
do_shell(){
# Execute command in shell, while logging complete command to stdout
    echo "$(date +%F_%T) --> $*"
    eval "$*"
    STATUS=$?
    return $STATUS
}

HOSTNAME=$1

if [[ -x /usr/bin/curl ]]; then
  until [ "`curl --silent --show-error --connect-timeout 1 -I http://$HOSTNAME/webconsole 2> /dev/null | grep 'Coyote'`" != "" ];
  do
    date_echo "waiting for tomcat to respond"
    sleep 1
  done
    date_echo "Tomcat is ready!"
else
  date_echo "waiting for tomcat to respond"
  do_shell "sleep 120"
  date_echo "Tomcat is most likely ready"
fi


