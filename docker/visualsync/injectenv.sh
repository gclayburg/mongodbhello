#!/bin/bash

iterate_secrets(){
  echo "checking secrets: $1"
  for file in $(ls $1); do
     myvars="${myvars} ${file}=$(cat $1/${file})"
     echo "adding secret: $file"
  done
  #i.e.,  env MONGOUSER=hi MONGOPASSWORD=supersecret runprog.sh

}
if [[ $# >0 ]]; then
  myvars=""
  if [[ -d $1 ]]; then
    iterate_secrets $1
    shift
  else
    if [[ -d /tmp/secrets ]]; then
      iterate_secrets /tmp/secrets
    fi
  fi
#  env ${myvars} /approot/runprog.sh "$@"
  env ${myvars} java -jar /approot/policyconsole.war "$@"

else
  echo "usage: $0 dir"
  echo ""
  echo "dir : directory to scan for environment injection"
  exit 1
fi
