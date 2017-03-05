FROM java:8-jre

MAINTAINER Orange Mi <orangemiwj@gmail.com>

ENV KAFKA_ADMIN_HOME=/kafka-admin-rest

ADD build/distributions/kafka-admin-rest.tgz /
WORKDIR /kafka-admin-rest
ADD docker/bin /kafka-admin-rest/bin/
ADD docker/config /kafka-admin-rest/config/

EXPOSE 9001
CMD bin/entrypoint.sh
