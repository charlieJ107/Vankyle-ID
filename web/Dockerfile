FROM node:latest AS node-builder
ARG PUBLIC_URL=/vankyle-id
ARG REACT_APP_API_URL=/api
ARG REACT_APP_OIDC_AUTHORITY=/vankyle-id
ARG REACT_APP_OIDC_REDIRECT_URI=vankyle-id/oidc
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
