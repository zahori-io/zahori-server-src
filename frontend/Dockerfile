# base image
FROM node:14.18.3-alpine as angularbuilder

RUN npm install -g @angular/cli@11.2.10

COPY package.json /code/package.json
WORKDIR /code
RUN npm install

COPY . /code/
RUN ng build --prod

FROM scratch
COPY --from=angularbuilder /code/dist/front /


