# Práctica 002

- Crear un `Dockerfile` para definir la imagen y el contenedor de la aplicación dada.

## Comandos

Para construir la imagen (sin utilizar ningún caché) compilando el Dockerfile, se ejecuta

```bash
docker build -t billingapp:prod --no-cache --build-arg JAR_FILE=target/*.jar .
```

donde `billingapp:prod` es el `<nombre de la imagen>:<Tag>`.

Para levantar el contenedor, haciendo el mapeo a puertos, se ejecuta

```bash
docker run -p 80:80 -p 8080:8080 --name billingapp billingapp:prod
```

donde `billingapp` en el nombre dado al contenedor.

## Navegador

- `http://localhost/` aplicación
- `http://localhost:8080/swagger-ui/index.html` interfaz gráfica del microservicio

