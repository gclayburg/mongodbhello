#!/bin/bash
date_echo(){
    datestamp=$(date +%F_%T)
    echo "${datestamp} $*"
}

iterate_secrets(){
  date_echo "checking secrets in directory: $1"
  for file in $(ls $1); do
     myvars="${myvars} ${file}=$(cat $1/${file})"
     echo "adding secret: $file"
  done
  #i.e.,  env MONGOUSER=hi MONGOPASSWORD=supersecret runprog.sh

}
if [[ $# >0 ]]; then
  myvars=""
  if [[ -d $1 ]]; then # first argument is a directory, lets assume kubernetes secrets are stored there
    iterate_secrets $1
    shift
  else
    if [[ -d /tmp/secrets ]]; then  #/tmp/secrets is the standard secret directory
      iterate_secrets /tmp/secrets
    fi
  fi
else
  if [[ -d /tmp/secrets ]]; then  #/tmp/secrets is the standard secret directory
    iterate_secrets /tmp/secrets
  fi
fi
#  env ${myvars} /approot/runprog.sh "$@"
export SOMEJUNK=hello
date_echo "env is: "
env
date_echo "myvars is: ${myvars}"
date_echo "which java"
date_echo "java -version"
date_echo "running war..."
date_echo "/usr/bin/java -jar /approot/policyconsole.war $@ "
exec "/usr/bin/java"  "-jar /approot/policyconsole.war $@"
