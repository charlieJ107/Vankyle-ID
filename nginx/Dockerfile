FROM nginx:latest
LABEL authors="Charlie J <charlie_j107@outlook.com>"
LABEL org.opencontainers.image.source = "https://github.com/charlieJ107/Vankyle-ID"
COPY reserve.nginx.conf.template /etc/nginx/templates/default.conf.template

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]