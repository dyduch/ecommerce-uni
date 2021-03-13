FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get update && apt-get install -y vim git wget unzip curl gnupg

# get java
RUN apt-get install -y openjdk-8-jdk

# get scala
RUN wget https://downloads.lightbend.com/scala/2.12.13/scala-2.12.13.deb
RUN dpkg -i scala-2.12.13.deb
RUN apt-get install -y scala

# get sbt
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt-get update
RUN apt-get install sbt

# get node
RUN curl -sL https://deb.nodesource.com/setup_14.x  | bash -
RUN apt-get -y install nodejs


RUN useradd -ms /bin/bash dyduch 
RUN adduser dyduch sudo

# expose port for react app nd play
EXPOSE 3000
EXPOSE 9000

USER dyduch
WORKDIR /home/dyduch
RUN mkdir /home/dyduch/projekt

VOLUME /home/dyduch/project-data

