FROM node:latest AS node-builder

COPY . /src
WORKDIR /src
RUN npm install
RUN npm run build

FROM nginx:alpine AS nginx-runtime
LABEL authors="Charlie J <charlie_j107@outlook.com>"

COPY --from=node-builder /src/build /usr/share/nginx/html
COPY frontend.nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
