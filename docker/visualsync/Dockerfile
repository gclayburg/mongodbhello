FROM registry:5000/java8:gjava8
#FROM dockerfile/java:oracle-java8
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

EXPOSE 8080
VOLUME ["/policy"]
RUN  mkdir /approot
ADD policyconsole.war /approot/

ENTRYPOINT ["java","-jar","/approot/policyconsole.war"]
