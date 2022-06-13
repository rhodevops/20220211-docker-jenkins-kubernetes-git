# Crear la imágenes de las aplicaciones definidas en cada Dockerfile

El nombre:tag de la imagen debe corresponderse con la que se indica
en el `Deployment` del microservicio en java (back)

```bash
docker build -t billingapp-back:0.0.4 --no-cache --build-arg JAR_FILE=./*.jar .
```

El nombre:tag de la imagen debe corresponderse con la que se indica
en el `Deployment` de la aplicación del front

```bash
docker build -t billingapp-front:0.0.4  --no-cache .
```

# Orquestación con kubernetes

Para utilizar las imágenes locales hay que ejecutar

```bash
minikube docker-env
```

y a continuación

```bash
eval $(minikube -p minikube docker-env)
```

Lo anterior solo tiene efecto en la terminal en la que se ejecuta.
Si se sale, ya no tiene efecto.
