# base image
FROM node:14.16.1-alpine as angularbuilder

RUN npm install -g @angular/cli@11.2.8  

COPY package.json /code/package.json
WORKDIR /code
RUN npm install

COPY . /code/
RUN ng build --prod

FROM scratch
COPY --from=angularbuilder /code/dist/front /


