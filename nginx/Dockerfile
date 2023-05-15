FROM nginx:latest
LABEL authors="Charlie J <charlie_j107@outlook.com>"

COPY reserve.nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]