FROM java:8-jre

MAINTAINER Orange Mi <orangemiwj@gmail.com>

ENV KAFKA_ADMIN_VERSION=0.1.2-SNAPSHOT
ENV KAFKA_ADMIN_HOME=/kafka-admin-${KAFKA_ADMIN_VERSION}
ADD build/distributions/kafka-admin-${KAFKA_ADMIN_VERSION}.tgz /
WORKDIR ${KAFKA_ADMIN_HOME}
ADD docker/bin ${KAFKA_ADMIN_HOME}/bin/
ADD docker/config ${KAFKA_ADMIN_HOME}/config/
ADD web ${KAFKA_ADMIN_HOME}/web/

EXPOSE 9001
CMD bin/entrypoint.sh
