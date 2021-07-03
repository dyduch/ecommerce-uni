FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get update && apt-get install -y vim git wget zip unzip curl gnupg

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


# get frontend part
RUN mkdir /home/dyduch/store-react-client
COPY ./store-react-client/package.json /home/dyduch/store-react-client/package.json
WORKDIR /home/dyduch/store-react-client
RUN ls -lah
RUN npm install
COPY ./store-react-client/ /home/dyduch/store-react-client/
RUN ls -lah

