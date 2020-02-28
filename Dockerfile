# Generate CPU load for the given number of threads and cores.
# Generate Load Average.
# Load.java forked from https://gist.github.com/SriramKeerthi/0f1513a62b3b09fecaeb
# Generate CPU Load with Memory Pressure.

FROM openjdk:8

# Gradle Installation.
# Cite: https://gordonlesti.com/building-a-gradle-docker-image
# Installing v6.3 as higher versions don't seem to work.
RUN wget -q https://services.gradle.org/distributions/gradle-6.3-bin.zip \
    && unzip gradle-6.3-bin.zip -d /opt \
    && rm gradle-6.3-bin.zip

ENV GRADLE_HOME /opt/gradle-6.3
ENV PATH $PATH:/opt/gradle-6.3/bin

# Copying the source files into the Docker container's root directory.
COPY . /

# Compile.
RUN gradle clean && gradle build

# Default = help string.
CMD ["-h"]

ENTRYPOINT ["./system-load-generator"]
