FROM nginx:alpine

# Copiamos el archivo index.html al directorio por defecto de Nginx
COPY index.html /usr/share/nginx/html/index.html

# Exponemos el puerto 80 dentro del contenedor
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
