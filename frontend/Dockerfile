# base image
FROM node:14.4.0 as angularbuilder

RUN npm install -g @angular/cli@10.2.0

COPY package.json /code/package.json
WORKDIR /code
RUN npm install

COPY . /code/
RUN ng build --prod

FROM scratch
COPY --from=angularbuilder /code/dist/front /


