FROM ubuntu
MAINTAINER Gary Clayburg <gclaybur@comcast.net>

RUN apt-get update
RUN apt-get upgrade -y

# needed to add ppa repository containing oracle java downloader/installer (ppa:webupd8team/java)
RUN apt-get -y install software-properties-common

# automatically accept oracle license
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections 
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections 
RUN add-apt-repository -y ppa:webupd8team/java 
RUN apt-get update 
RUN apt-get install -y oracle-java7-installer

RUN update-alternatives --display java
ENV JAVA_HOME /usr/lib/jvm/java-7-oracle

