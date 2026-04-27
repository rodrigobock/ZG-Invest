FROM node:24-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration production

FROM nginx:alpine
# Copia a build do angular da pasta definida no angular.json (dist/frontend-zg)
COPY --from=build /app/dist/frontend-zg /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
