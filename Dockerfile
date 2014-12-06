FROM gclayburg/tomcat7
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

RUN apt-get install -y build-essential libcurl4-openssl-dev libxml2-dev mime-support

RUN mkdir /groovyroot 
#ADD  policyconsole-0.3.war /var/lib/tomcat7/webapps/
ADD policyconsole/target/policyconsole.war /var/lib/tomcat7/webapps/ 

CMD service tomcat7 start && tail -100f /var/lib/tomcat7/logs/catalina.out 
