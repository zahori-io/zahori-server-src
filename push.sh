#!/bin/bash
set DOCKER_BUILDKIT=1
docker tag zahoriaut/zahori-server:0.1.2 zahoriaut/zahori-server:0.1.2
docker push zahoriaut/zahori-server:0.1.2
