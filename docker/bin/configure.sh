#!/usr/bin/env bash
set -e
for VAR in `env`
do
  if [[ $VAR =~ ^KAFKA_ADMIN_ ]]; then
    config_name=`echo "$VAR" | sed -r "s/KAFKA_ADMIN_(.*)=.*/\1/g" | tr '[:upper:]' '[:lower:]' | tr _ .`
    env_var=`echo "$VAR" | sed -r "s/(.*)=.*/\1/g"`
    if egrep -q "(^|^#)$config_name=" $KAFKA_ADMIN_HOME/config/config.properties; then
      sed -r -i "s@(^|^#)($config_name)=(.*)@\2=${!env_var}@g" $KAFKA_ADMIN_HOME/config/config.properties #note that no config values may contain an '@' char
    else
      echo "$config_name=${!env_var}" >> $KAFKA_ADMIN_HOME/config/config.properties
    fi
  fi
done
