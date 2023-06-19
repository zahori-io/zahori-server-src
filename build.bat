set DOCKER_BUILDKIT=1
docker build -o backend/src/main/resources/static/ frontend
docker build -t zahoriaut/zahori-server:0.1.17 backend
