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
EXPOSE 3000

USER dyduch
WORKDIR /home/dyduch


# get frontend part
RUN mkdir /home/dyduch/store-react-client
COPY ./package.json /home/dyduch/store-react-client/package.json
WORKDIR /home/dyduch/store-react-client
RUN ls -lah
RUN npm install
COPY . /home/dyduch/store-react-client/
RUN ls -lah