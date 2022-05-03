#!/bin/bash

./build_docker_image.sh

docker run -it -p 8080:8080 -e HOST_IP=127.0.0.1  poc_compile_time_caliban_client_generation:0.0.1