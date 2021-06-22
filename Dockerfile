FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get update && apt-get install -y vim git wget zip unzip curl gnupg

# get java
# RUN apt-get install -y openjdk-8-jdk

# get scala
# RUN wget https://downloads.lightbend.com/scala/2.12.13/scala-2.12.13.deb
# RUN dpkg -i scala-2.12.13.deb
# RUN apt-get install -y scala

# get sbt
# RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
# RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
# RUN apt-get update
# RUN apt-get install sbt

# RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
# RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
# RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
# RUN apt-get update
# RUN apt-get install sbt

# get node
RUN curl -sL https://deb.nodesource.com/setup_14.x  | bash -
RUN apt-get -y install nodejs


RUN useradd -ms /bin/bash dyduch 
RUN adduser dyduch sudo

# expose port for react app nd play
EXPOSE 8080
EXPOSE 5000
EXPOSE 9000



USER dyduch
WORKDIR /home/dyduch

# java scala and sbt with sdk man
RUN curl -s "https://get.sdkman.io" | bash
RUN chmod a+x "/home/dyduch/.sdkman/bin/sdkman-init.sh"
RUN bash -c "source /home/dyduch/.sdkman/bin/sdkman-init.sh && sdk install java 8.0.272.hs-adpt"
RUN bash -c "source /home/dyduch/.sdkman/bin/sdkman-init.sh && sdk install sbt 1.5.1"
RUN bash -c "source /home/dyduch/.sdkman/bin/sdkman-init.sh && sdk install scala 2.12.13"

RUN mkdir /home/dyduch/projekt

VOLUME /home/dyduch/project-data

