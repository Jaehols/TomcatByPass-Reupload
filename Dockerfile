FROM tomcat:9.0.68-jdk11-temurin-jammy

RUN  apt-get update -y && \
     apt-get install -y vim \
    unzip

# Yourkit setup: https://www.yourkit.com/docs/java/help/docker.jsp
RUN wget https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2022.3-docker.zip -P /tmp/ && \
  unzip /tmp/YourKit-JavaProfiler-2022.3-docker.zip -d /usr/local && \
  rm /tmp/YourKit-JavaProfiler-2022.3-docker.zip

# Add java arguments for Yourkit
RUN echo "JAVA_OPTS=\"-agentpath:/usr/local/YourKit-JavaProfiler-2022.3/bin/linux-x86-64/libyjpagent.so=port=10001,listen=all\"" > $CATALINA_HOME/bin/setenv.sh && \
    chmod +x $CATALINA_HOME/bin/setenv.sh
