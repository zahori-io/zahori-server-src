#!/bin/bash
set DOCKER_BUILDKIT=1
docker tag zahoriaut/zahori-server:0.1.4-SNAPSHOT zahoriaut/zahori-server:0.1.4-SNAPSHOT
docker push zahoriaut/zahori-server:0.1.4-SNAPSHOT
