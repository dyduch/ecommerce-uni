FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y vim git wget unzip

# get java
RUN apt-get install -y openjdk-8-jdk

# get scala
RUN wget https://downloads.lightbend.com/scala/2.12.13/scala-2.12.13.deb
RUN dpkg -i scala-2.12.13.deb
RUN apt-get install scala

RUN useradd -ms /bin/bash dyduch 
RUN adduser dyduch sudo

EXPOSE 8888

USER dyduch
WORKDIR /home/dyduch
RUN mkdir /home/dyduch/projekt

VOLUME /home/dyduch/projekt

