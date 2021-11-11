FROM maven:3.6-jdk-11 as builder
WORKDIR app
COPY . /app
RUN mvn clean package

FROM apache/nifi:1.12.1
ENV NIFI_HOME=/opt/nifi/nifi-current
USER nifi
COPY --chown=nifi:nifi /config/flow.xml.gz ${NIFI_HOME}/conf
COPY --chown=nifi:nifi /config/demo.properties ${NIFI_HOME}/conf
COPY --chown=nifi:nifi --from=builder app/nifi-sagemcom-processor/target/nifi-sagemcom-processor-*.nar ${NIFI_HOME}/lib
