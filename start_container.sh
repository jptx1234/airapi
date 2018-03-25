#!/usr/bin/env bash

docker run -ti \
-e TZ=Asia/Shanghai --restart always \
-d --name jptx-airapi \
-p 9001:8080 jptx/airapi
