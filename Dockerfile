FROM java:8-jre

MAINTAINER Orange Mi <orangemiwj@gmail.com>

ADD build/distributions/kafka-admin-rest.tgz /
WORKDIR /kafka-admin-rest

VOLUME ["/kafka-admin-rest/config"]
EXPOSE 9001
CMD ["bin/kafka-admin-rest", "config/config.properties"]
