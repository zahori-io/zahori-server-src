#!/bin/bash
DOCKER_BUILDKIT=1 docker build -o backend/src/main/resources/static/ frontend
DOCKER_BUILDKIT=1 docker build -t zahoriaut/zahori-server:0.1.13 backend
