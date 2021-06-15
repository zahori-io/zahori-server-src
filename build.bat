set DOCKER_BUILDKIT=1
docker build -o backend/src/main/resources/static/ frontend
docker build -t zahori-server:0.1.6 backend
