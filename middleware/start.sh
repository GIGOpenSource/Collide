#!/bin/bash

if [ ! -n "$1" ] ;then
    echo "要修改的ip为空"
else
    echo "ip为：$1"
    sed -i "s/172.16.0.104/$1/g" docker-compose.yml
    sed -i "s/172.16.0.104/$1/g" nacos/env/nacos-standlone-mysql.env
    sed -i "s/172.16.0.104/$1/g" rocketMQ/data/broker/conf/broker.conf
    sed -i "s/172.16.0.104/$1/g" xxl-job/config/application.properties
    sed -i "s/172.16.0.104/$1/g" kibana/conf/kibana.yml
    sed -i "s/172.16.0.104/$1/g" seata-server/resources/application.yml
fi
