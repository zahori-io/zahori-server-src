#!/bin/bash
set DOCKER_BUILDKIT=1
docker tag zahoriaut/zahori-server:0.1.6 zahoriaut/zahori-server:0.1.6
docker push zahoriaut/zahori-server:0.1.6
