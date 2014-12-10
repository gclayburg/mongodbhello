#!/bin/bash
cp ../../policyconsole/target/policyconsole*.war policyconsole.war
exec "$@"
