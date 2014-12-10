FROM gclayburg/java7
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

EXPOSE 8080
VOLUME ["/policy"]
RUN  mkdir /approot
ADD policyconsole*.war /approot

ENTRYPOINT ["java","-jar","/approot/policyconsole*.war"]
