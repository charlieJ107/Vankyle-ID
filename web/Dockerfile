FROM node:latest AS node-builder
COPY . /src
WORKDIR /src
RUN npm install
RUN npm run build

FROM nginx:alpine AS nginx-runtime
LABEL authors="Charlie J <charlie_j107@outlook.com>"
LABEL org.opencontainers.image.source = "https://github.com/charlieJ107/Vankyle-ID"
COPY --from=node-builder /src/build /usr/share/nginx/html
COPY frontend.nginx.conf.template /etc/nginx/templates/default.conf.template
EXPOSE 80
