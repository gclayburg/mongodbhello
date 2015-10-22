FROM registry:5000/java8:gjava8
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

EXPOSE 8080
VOLUME ["/policy"]
RUN  mkdir /approot

RUN apt-get update && apt-get install -y git

COPY ./injectenv.sh /approot/
RUN chmod 755 /approot/injectenv.sh
COPY policyconsole.war /approot/
ENTRYPOINT ["/approot/injectenv.sh"]
