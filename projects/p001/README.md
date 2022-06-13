# Práctica 001

- Buscar en Docker Hub la imagen de `postgres`.
- En el repositorio de `posgres` se encuentra el código del archivo `.yml` que vamos a utilizar con `docker-compose`.
- Guardar el archivo como `docker-compose.yml`.
- Editar el archivo: añadir nombre al contenedor, cambiar el número de puerto (`9090:8080`), añadir la dependecia de `adminer` respecto a `db`.
- Ejecutar el docker-compose

## Comandos

```bash
docker-compose up -d
```

## Navegador

- url: [localhost:9090](localhost:9090) con las credenciales indicadas en el docker-compose

