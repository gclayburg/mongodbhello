#!/bin/bash
date
cp ../../policyconsole/target/policyconsole*.war policyconsole.war
date
exec "$@"
date
