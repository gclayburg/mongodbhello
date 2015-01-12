
Building

basic build

```
$ mvn install
```

basic smoke test of REST webservices on running tomcat 7 server and embedded mongo db

```
$ mvn -Dmaven.test.skip=true install -Psmokeprofile -Psmoke-tomcat7
```

run smoke test against a server already running on host mink, port 8089, without building everything else

```
$ mvn --projects smoketest -Psmokeprofile -Dendpoint=mink:8089 integration-test
```