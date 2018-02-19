# Generate CPU load for the given number of threads and cores.
# Load.java forked from https://gist.github.com/SriramKeerthi/0f1513a62b3b09fecaeb

FROM java:8
## using latest recommended ubuntu version as the base image
#FROM ubuntu:xenial
#
#ENV JAVA_VER 8
#ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
#
## Updating source list
#RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list
#RUN apt-get -y update
## Enable add-apt-respository
#RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-software-properties software-properties-common
## Installing java8
#RUN echo 'deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
#    echo 'deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
#    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886 && \
#    apt-get update && \
#    echo oracle-java${JAVA_VER}-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections && \
#    apt-get install -y --force-yes --no-install-recommends oracle-java${JAVA_VER}-installer oracle-java${JAVA_VER}-set-default && \
#    apt-get clean && \
#    rm -rf /var/cache/oracle-jdk${JAVA_VER}-installer
#
## Set Oracle Java as the default Java
#RUN update-java-alternatives -s java-8-oracle
#RUN echo "export JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> ~/.bashrc

# Cleaning
# RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Copying the source files into the Docker container's root directory.
COPY . /
# Compiling the source files.
RUN javac SimulateConstIncreaseCPULoad.java

# Generate CPU load with step size 1%.
# Please note that this is a default run command.
# Further command line arguments can be given.
CMD java SimulateConstIncreaseCPULoad 0.01

# Generate CPU load and maintain the CPU load for the given duration (milliseconds).
# CMD java SimulateConstIncreaseCPULoad 0.01 4000

# Generate alternating CPU load, maintain it for a given duration. Using the default number of segments (2).
# CMD java SimulateConstIncreaseCPULoad 0.01 4000 true

# Generate alternating CPU load, maintain it for a given duration, and specify the number of segments.
# CMD java SimulateConstIncreaseCPULoad 0.01 4000 true 4

