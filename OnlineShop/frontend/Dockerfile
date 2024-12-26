FROM node:15.12.0-alpine as build
WORKDIR /app

COPY package.json package-lock.json ./
COPY nginx/ ./

RUN chmod +x ./script.sh
RUN npm install

COPY . ./
RUN npm run build

RUN apk add dos2unix
RUN dos2unix ./*
RUN dos2unix ./*/*

FROM nginx:stable-alpine
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=nginx:nginx /app/build /usr/share/nginx/html
COPY --from=build --chown=nginx:nginx /app/script.sh /script.sh
COPY --from=build --chown=nginx:nginx /app/nginx-selfsigned.crt /etc/nginx/nginx-selfsigned.crt
COPY --from=build --chown=nginx:nginx /app/nginx-selfsigned.key /etc/nginx/nginx-selfsigned.key
EXPOSE 80

RUN apk add dos2unix
RUN dos2unix /usr/share/nginx/html
RUN dos2unix /script.sh
RUN dos2unix /etc/nginx/nginx-selfsigned.crt
RUN dos2unix /etc/nginx/nginx-selfsigned.key

ENTRYPOINT ["/script.sh"]
CMD ["nginx", "-g", "daemon off;"]
