FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get update && apt-get install -y vim git wget zip unzip curl gnupg

# get java
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
RUN export JAVA_HOME

# get scala
RUN wget https://downloads.lightbend.com/scala/2.13.6/scala-2.13.6.deb
RUN dpkg -i scala-2.13.6.deb

# get sbt
RUN mkdir -p "/usr/local/sbt"
RUN wget -qO - --no-check-certificate "https://github.com/sbt/sbt/releases/download/v1.5.2/sbt-1.5.2.tgz" | tar xz -C /usr/local/sbt --strip-components=1

RUN useradd -ms /bin/bash dyduch 
RUN adduser dyduch sudo

# expose port for react app nd play
EXPOSE 9000

USER dyduch
WORKDIR /home/dyduch

COPY . /home/dyduch/store-play-server/
WORKDIR /home/dyduch/store-play-server
