FROM registry:5000/java8:gjava8
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

EXPOSE 8080
VOLUME ["/policy"]
RUN  mkdir /approot
ADD policyconsole.war /approot/

RUN apt-get update && apt-get install -y git

#ENTRYPOINT ["java","-jar","/approot/policyconsole.war"]
ADD ./injectenv.sh /approot/
RUN chmod 755 /approot/injectenv.sh
ENTRYPOINT ["/approot/injectenv.sh"]
