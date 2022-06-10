---
title: "DevOps con Docker, Jenkins, Kubernetes, git, GitFlow CI y CD"
author: Roberto Negro
date: 11 de enero de 2022
---

# Sobre este curso

## Información del curso

Enlace del curso: [DevOps con Docker, Jenkins, Kubernetes, git, GitFlow CI y CD](https://www.udemy.com/course/devops-con-dockers-kubernetes-jenkins-y-gitflow-cicd/)

## Conocimientos previos

Para seguir correctamente estos apuntes, se necesita algunos conocimientos previos:

- Instalar docker, preferiblemente en entorno linux.
- Comandos básicos de docker y docker-compose.
- Tener cuenta en `docker hub`.

## Workspace

- Los archivos de las practicas se encuentra en `.\practices`.
- Las prácticas se desarrollan en una máquina virtual de virtualbox con ubuntu. 
Los archivos se encuentra en una ruta homóloga.

## Una nota muy importante

[Muchos problemas vienen dados por los permisos](url-tapadera)

Es posible que algunos archivos (como el `.jar`) se corrompen cuando 
se accede a ellos desde virtual box a través de una carpeta compartida 
con el host windows. Esto podría tener que ver con los permisos de las
carpetas, pero no lo tengo claro. Una manera de evitarlo es compartir 
los archivos en formato comprimido y moverlos dentro de ubuntu a
una carpeta no compartida y modificando los permisos de carpete como
corresponda. Aún así, en algunas de las prácticas hacer esto no
es suficiente. 

Problemas detectados al ejecutar docker:

- Falla la conexión a la base de datos postgres
- No existe permiso para acceder a una carpeta de volumen

Algunas cosas que podemos mirar para solucioanr el problema:

- ¿El dueño del directorio/archivo es root o el usuario?
- ¿Qué permisos tiene el directorio/archivo?
- ¿Que pérmisos/dueños otorga Docker a los volúmenes que crea? 
- ¿Es mejor crear la carpeta del volumen a mano para controlar los permisos?

Algunas soluciones que podría valer cuando docker crea un volumen 
en `/var/lib/<volumen>` asignando el propietario `systemd-coredump:root`

- Cambiar los permisos de los directorios a `755`
- Añadir `systemd-coredump` al grupo docker
- Cambiar el propietario del volumen a `root-root`
- Crear un `.dockerignore` en el directorio del proyecto
incluyendo la ruta del volumen.

## Otras notas aclaratorias

Aclaraciones para realizar las prácticas:

- Cuidado con las rutas. En windows se utiliza el backslash `\` y en linux el slash `/`.
- Hay que ejecutar los comandos desde el directorio adecuado.
- En windows, haqu que utilizar las órdenes de powershell equivalentes a las de bash.
- [Muchos problemas vienen dados por los permisos](url-tapdera)

Trabajando en una máquina virtual de linux:

# Introducción

## El escenario típico de las empresas que aún no implementan DevOps

Existe un muro entre el equipo de desarrollo y el equipo de operadores. 
Las consecuencias son las siguientes:

- Calendario de releases fijo.
- Puesta en producción más lenta.
- Fricción entre los equipos.
- Baja automatización.
- Scripts rudimentarios.

## Herramientas que se ven en el curso

Se recomienda realizar el curso utilizando una distribución de linux.

Herramientas de development:

- `Repositories`
- `Gitflow`
- `Github`
- `Microservices`
- `TDD` Test driven development

Herramientas de operation:

- `Docker`
- `Kubernetes`
- `Prometheus`
- `Sonarqube`
- `Yaml`

Flujo de trabajo recomendado:

- Estudiar conceptos
- Preparar el entorno de desarrollo
- Realizar las prácticas
- Probar el código
- Depurar el código
- Leer material de apoyo, resolver dudas

## ¿Qué es DevOps?

[Una lectura recomendada para entender que es devops.](https://azure.microsoft.com/es-es/overview/devops-tutorial/#understanding)

Se busca la unión de los equipos y la automatización y optimización del proceso:

- Es una cultura de empresa
- El objetivo es dar valor al cliente (alto rendimiento, entrega rápida)
- Involucra a desarrolladores, operaciones, calidad y seguridad.
- Se utilizan las herramientas nombradas en la sección anterior.

El Site Reliability Engineer (SRE) es la persona encargada de las funciones devops.

## Aspectos clave de DevOps

- `Control de versiones` git $\to$ github, gitlab, bitbucket, mercurial
- `Integración continua` Pipelines jenkins $\to$ automatizar compilaciones
- `Infraestructura como código` Terraforn $\to$ archivos de definición basados en texto (.yaml)
- `Supervisión y registro` prometheus, grafana $\to$ monitorizar y recopilar métricas
- `Aprendizaje validado` análisis de datos

## Estructura del curso y objetivos

- 50% operaciones
- 50% desarrollo

# Conceptos sobre docker

## Los contenedores

Conceptos clave:

- Aislar la aplicación y las librerías de las que depende en un contenedor.
- Resuelve el problema de las dependencias en distintos SO.
- Docker engine es el motor de ejecución de contenedores.
- Docker Hub es el repositorio de imágenes por defecto.
- Docker-compose es un orquestador ligero de contenedores.
- Kubernetes es un sistemas para administrar clusters y es un orquestador empresarial de contenedores.

## Comunicación externa e interna en un entorno de contendores

Distinguir claramente dos partes:

- La comunicación $\to$ `docker engine`
- El almacenamiento $\to$ `volúmenes` (locales y en la nube)

Esquemas:

- `host` dirección ip
- `cliente` petición al host a través de internet
- `docker engine` maneja la petición
- `contenedores` puertos `<externo:interno>`
- `volúmenes` mapeo `<host:contenedor>` o `<cloud:contenedor>`

## Instalar Docker engine y Docker compose en tu SO

Consultar la documentación oficial y/o los apuntes de docker.

Ejecutar `docker run hello-world` para verificar que la instalación de docker es correcta.


# Primeros pasos con docker

## Caso de estudio y diagrama de despliegue para una aplicación en tres entornos

Se requiere desplegar la aplicación de facturación de la compañía, 
en los entornos de integración, preproducción y producción. La instalación debe contar con 
alta disponibilidad, excepto en integración.

Los requisitos técnicos son:

- `Java 1.8`
- Servidor Web `Nginx`
- Base de datos `Postgres`

Ver diagrama de la infraestructura en el video del curso:

- El Frontend es en `Angular`
- El Backend es en `java (microservicios)`

Notas:

- Cuidado, la aplicación no está pensada para manejar excepciones.
- En cliente se debe colocar un id.
- En el navegador, accediendo a inspeccionar con `F12` podemos ver el error.

## Crear el contenedor para la app de facturación

Para descargar la imagen `sotobotero/udemy-devops:0.0.1.`, alojada en docker hub, se ´
ejecuta:

```bash
docker pull sotobotero/udemy-devops:0.0.1
```

y para levantar el contenedor asignando el puerto `80` a la aplicación en angular y el puerto `8080` 
al microservicio, se ejecuta

```bash
docker run -p 80:80 -p  8080:8080 --name billingapp sotobotero/udemy-devops:0.0.1
``` 

donde `billingapp` es el nombre dado al contenedor. 

En el navegador vamos a acceder a:

- `http://localhost/` aplicación
- `http://localhost:8080/swagger-ui/index.html` interfaz gráfica del microservicio

Nos piden usuario y contraseña e introducimos `admin` y `admin`.

- En la aplicación, introducir algunos datos de factura.
- En el microservicio, probamos el método `GET` (`Try it out` y `Execute`)

## Notas de buenas prácticas

- No es una buena práctica incluir los dos servicios anteriores en una misma imagen.

## Docker compose y orquestar un servicio con dos imágenes

Directorio de la práctica: `~\practices\p001`   

Buscamos en Docker Hub la imagen de `postgres` (verificar que es la imagen oficial). 
Postgres es una base de datos.

Pasos:

- En el repositorio Docker Hub de `postgres` se encuentra el código `.yml` estándar para
utilizar con `docker-compose`.
- Se crea el archivo `docker-compose.yml` y se editan algunos campos.
- Se añade nombre al contenedor, se cambia el número de puerto (`9090:8080`), se añade 
la dependecia del servivio `adminer` respecto al servicio `db`.


Arhivo `docker-compose.yml`

```yml
# Use postgres/qwerty user/password credentials
version: '3.1'

services:

  db:
    container_name: postgres
    image: postgres
    restart: always
    environment:
    ports:
      - 5432:5432
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: qwerty
      POSTGRES_DB: postgres    

  adminer:
    container_name: adminer
    image: adminer
    restart: always
    depends_on: 
      - db
    ports:
       - 9090:8080
```

Recordatorio:

- `docker-compose pull` descarga las imágenes del archivo `docker-compose.yml`
- `docker-compose -f <docker-compose file> pull` idem anterior con otro nombre de archivo yaml
- `docker-compose up -d` levanta los contenedores del archivo `docker-compose.yml`
- `docker-compose -f <docker compose file> up -d` idem anterior con otro nombre de archivo yaml

Terminamos de verificar lo anterior navegando a `localhost:9090`. Se utilizan las
credenciales definidas en el archivo yaml.

## Crear una imagen personalizada

Directorio de la práctica: `~\practices\p002`   

Los archivos de la practica:

- `dist` contiene la aplicación en angular
- `target` contiene el archivo `.jar` del microservicio de java
- `appsshell.sh` es un script para el despliegue
- `nginx.conf` es la configuración por defecto del servidor web para que corra la aplicación

Nuestro objetivo es crear el `Dockerfile`, un archivo  de texto plano que recoge 
las instrucciones necesarias para construir la imagen. El `Dockerfile` es lo único que 
necesitas para mover una aplicación de un sistema a otro.

Fichero `Dockerfile`:

```python
# imagen base
FROM nginx:alpine

# instalar java 8
RUN apk -U add openjdk8 \ 
    && rm -rf /var/cache/apk/*;
RUN apk add ttf-dejavu 

# instalar el microservicio de java (.jar)
ENV JAVA_OPTS=""
ARG JAR_FILE # argumento utilizado en el docker build para indicar la ruta del archivo .jar
ADD ${JAR_FILE} app.jar # asigna un nombre al archivo jar

# utiliza como volumen el /tmp del so host
VOLUME /tmp
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/nginx.conf # copia fichero de configuración
COPY dist/billingApp /usr/share/nginx/html # copia la aplicación de angular en el nginx
COPY appshell.sh appshell.sh # copia script que se utiliza al levantar el contenedor

# expose ports 8080 for java swagger app and 80 for nginx app
EXPOSE 80 8080

# se ejecuta en el momento de levantar/inicializar el contenedor
ENTRYPOINT ["sh", "/appshell.sh"] 
```

Algunos comandos del Dockerfile:

- `FROM` para indicar la imagen base de la cual se parte para crear la nueva imagen
- `RUN` para ejecutar instrucciones propias del sistemas operativo cuando se está 
construyendo la imagen
- `CMD` para especificar el comando por defecto que se ejecuta al iniciar el 
contenedor si no se especifica ningún servicio como argumento
- `ENTRYPOINT` idem anterior pero trata de distinto modo los argumentos

Para construir la imagen (sin utilizar ningún caché) compilando el Dockerfile, se ejecuta

```bash
docker build -t billingapp:prod --no-cache --build-arg JAR_FILE=target/*.jar .
```

donde `billingapp:prod` es el `<nombre de la imagen>:<Tag>`.

Para levantar el contenedor, haciendo el mapeo a puertos, se ejecuta

```bash
docker run -p 80:80 -p 8080:8080 --name billingapp billingapp:prod`
```

donde `billingapp` en el nombre dado al contenedor. Navegamos a `localhost`
para verificar el proceso anterior.

Para subir la imagen a `DockerHub`, creamos un repositorio llamado `rhonegro/billingapp`,
iniciamos sesión en la terminal ordenando `docker login`, y hacemos un tag y un push de la
imagen local

```bash
docker tag billingapp:prod rhonegro/billingapp:0.0.1
docker push rhonegro/billingapp:0.0.1
```

# Conceptos de docker, docker-compose y kubernetes

## Componentes de Docker y kubernetes

- `Docker` o `Docker engine` es la tecnología para crear contenedores.
- `Dockerfile` es el archivo de instrucciones para definir una imagen.
- `Docker-compose` es un sistema de orquestación de contenedores, es decir, un sistema para
definir servicios que requieren múltiples imágenes y contenedores en una misma red. 
Se utiliza en entornos locales o empresas pequeñas.
- `Kubernetes` es otro sistema de orquestación de contenedores. Es más potente y se utiliza en empresas grandes.

## Notas de buenas prácticas

- Una buena práctica es crear una imagen para cada servicio y despúes hacer la orquestación.
- No se recomienta el uso de `Docker Desktop` en un entorno profesional.
- Hay que tener en cuenta las versiones de los componentes anteriores y la compatibilidad entre ellos.

Consultar la [tabla de compatibilidades entre componentes.](https://docs.docker.com/compose/compose-file/compose-versioning/) 

## Host

- El sistema `host` es el equipo, el entorno de trabajo, la  máquina donde estamos trabajando. 
En un entorno empresarial el `host` es un servidor o un cluster de servidores.
- Un `cluster de servidores` es la unión de varios sistemas informáticos (servidores) que 
funcionan como si fueran uno solo. La idea es que se comparten recursos de hardware y 
software con el objetivo de ofrecer velocidad y sobre todo alta disponibilidad ante fallos.
- En el contexto de este curso, el host es un `cluster de kubernetes`, que es un 
conjunto de máquinas de nodos que ejecutan aplicaciones en contenedores. Puede 
tratarse de un cluster de la empresa o gestionado por un proveedor (AWS, Azure, etc)

Vamos a distinguir entre:

- El `host`, donde tenemos:
    - `Docker Image` imágenes
    - `FileSystem` de linux (Home, Databases, var/tmp) o windows (Program Files, Users, Downloads).
    - `Docker Engine` múltiples contenedores
- `IP + Port`

## Volúmenes en docker

Es muy importante tener claro que los contenedores son efímeros y se dehberían poder borrar sin mayor problema.
La persistencia debe estar en el FileSystem o en alguna nube.

- Los datos relevantes de nuestro sistema están montados como volúmenes. 
- Los volúmenes se utilizan para guardar los datos de las aplicaciones 
y mantenerlos en el sistema aunque se elimina la aplicación.
- Un volumen es almacenamiento estático para los contenedores y
es independiente del ciclo de vida del contenedor.

Mapeo de volúmenes:

- El volumen se tiene que mapear desde el sistema host al contenedor. 
- La estructura del mapeo es `<local filesystem path>:<container mount path>`
- Ejemplo en linux: `/home/bds/postgres:/var/lib/postgresql/data`

Problemas que podemos encontrarnos:

- Problemas debido a la ruta en el filesystem. Hay que revisar que se cree el archivo del volumen.
- Si un volumen ya contiene datos, no se puede volver a crear (no se reescribe).
- Cuando se elimina un contenedor, no se elimina su volumen.
- Los volúmenes se puede eliminar accediendo al directorio donde se encuentran. 
- Antes hay que detener el contenedor o contenedores que estén usando el volumen. 
- Buscar en internet la ruta donde se encuentran los volúmenes en windows.

## Puertos en docker

Cada servicio tiene un puerto de exposición. Los contenedores tiene puertos
internos y externos.

Mapeo de puertos:

- La estructura  es `<local port>:<container port>` 
- Ejemplo 1: `8080:8080`
- Ejemplo 2: `8082:8080` donde el puerto del host `8082` (externo) redirige 
la petición al puerto del contenedor `8080` (interno)

Oberservar que los puertos del host están redirigiendo la petición al mismo
puerto interno, pero esto no quiere decir que se envía al mismo contenedor 
(el docker engine sabe a que contendor tiene que redirigir la petición).

# Orquestación y escalado de contenedores con docker-compose

## Orquestación con imágenes independientes

Directorio de la práctica: `~\practices\p003`   

Notas:

- La app de java empaquetada en el `.jar` ha sido modificado por el
autor del cursos para conectar con la base de datos, luego no sirve
utilizar el achivo `.jar` de la práctica anterior.

Archivo docker-compose:

- Consta de 4 servicios:

```yaml
version: '3.1'

services:
#database engine service
  postgres_db:
    container_name: postgres
    image: postgres:latest
    restart: always
    ports:
      - 5432:5432
    volumes:
        #allow *.sql, *.sql.gz, or *.sh and is execute only if data directory is empty
      - ./dbfiles:/docker-entrypoint-initdb.d
      - /var/lib/postgres_data:/var/lib/postgresql/data
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: qwerty
      POSTGRES_DB: postgres    
#database admin service
  adminer:
    container_name: adminer
    image: adminer
    restart: always
    depends_on: 
      - postgres_db
    ports:
       - 9090:8080
#Billin app backend service
  billingapp-back:
    build:
      context: ./java
      args:
        - JAR_FILE=*.jar
    container_name: billingApp-back      
    environment:
       - JAVA_OPTS=
         -Xms256M 
         -Xmx256M         
    depends_on:     
      - postgres_db
    ports:
      - 8080:8080 
#Billin app frontend service
  billingapp-front:
    build:
      context: ./angular 
    container_name: billingApp-front
    depends_on:     
      - billingapp-back
    ports:
      - 80:80 
```

## Limitar y monitorizar recursos físicos del host a los contenedores

Se pude monitorizar los recursos (RAM, CPU) utilizados por los contenedores con el comando:

```bash
docker stats
```

Se muestra una salida como la siguiente:

```bash
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT        
5dc78701e70e   billingApp-front   0.00%     1.758MiB / 7.707GiB      
86c0a48feba1   adminer            0.00%     3.824MiB / 7.707GiB      
16e22fe247de   billingApp-back    0.14%     245.9MiB / 7.707GiB      
7ca8fe46904c   postgres           0.00%     34.2MiB / 7.707GiB       
```

```bash
MEM %  NET I/O          BLOCK I/O         PIDS
0.02%  8.5kB / 0B       28.7kB / 8.19kB   2
0.05%  8.58kB / 0B      0B / 0B           1
3.12%  101kB / 29.3kB   86kB / 0B         33
0.43%  38.4kB / 92kB    455kB / 766kB     17    
```

Podemos añadir el campo `service.deploy` en cada `service` definido en el docker-compose
de la práctica anterior. En este campo definimos los revurso que puede utilizar el servicio:

- `resources.limits` valores límites que puede utilizar
- `resources.reservations` valores mínimos reservados

Se muesta el ejemplo para el servicio `billingapp-front`

```yml
  billingapp-front:
    build:
      context: ./angular 
    container_name: billingApp-front
    depends_on:     
      - billingapp-back
    ports:
      - 80:80 
    deploy:
      resources:
        # valores límite
        limits:
          cpus: "0.15"
          memory: 250M
        # valores reservados: debe ser menor 
        # que el valor límite correspondiente  
        reservations:
          cpus: 0.1
          memory: 128M
```

Ahora al ejecutar `docker stats` se muestras las limitaciones impuestas.

Investigar en que contextos se utiliza lo anterior.

## Redes virtuales en docker y múltiples entornos

Directorio de la práctica: `~\practices\p004`   

Distintas redes para separar distintas aplicaciones o distintos entornos.

## Docker Swarm. Conceptos y activación del clúster

[¡PENDIENTE!](url-tapadera)

## Escalado de contenedores con docker Swarm

[¡PENDIENTE!](url-tapadera)

## Desplegar y probar el stack de contenedores con docker swarm

[¡PENDIENTE!](url-tapadera)

## Desactivar el cluster y volver a modo normal

[¡PENDIENTE!](url-tapadera)

# Cluster de kubernetes

## ¿Qué es kubernetes?

Es la solución empresarial para la orquestación de contenedores.

- Gestionar infraestructuras grandes
- Lo utilizan las grandes compañías

## Escalabilidad vertical y horizontal

La escalabilidad es la capacidad de los sistemas para adaptarse al crecimiento y a una mayor demanda.

- El `escalado vertical` se basa en agregar más recursos (procesamiento, memoria, almacenamiento) 
al mismo nodo y aumentar el poder de cómputo.
- El `escalado horizontal` se basa en aagregar más nodos que se adapten a la carga de trabajo

Es concepto de escalado horizontal nos lleva al concepto de `cluster` y a la idea de utilizar herramientas como kubernetes.

Un cluster es un grupo de nodos (no necesariamente es un grupo de servidores). Hablaremos de cluster de kubernetes.

## Sobre kubernetes

Es un software o sistema open-source que permite gestionar un cluster.

- Es bastante nuevo
- Creado en el 2015
- Fundamental en devops

## Arquitectura de un cluster de kubernetes

Un clúster de kubernetes se compone de varias máquinas llamadas nodos:

- `Nodos maestros` alojan los componentes que controlan el clúster, constituyen el `control plane`
- `Nodos workers` alojan los pods que son los componentes de carga de la aplicación

## Arquitectura de un nodo maestro

Componentes de un nodo maestro:

- `etcd` es la base de datos del cluster de kubernetes
- `scheduler` controla las solicitudes, selecciona el nodo para los pods y los ejecuta
- `controller manager` supervisa controladores más pequeños que ejecutan tareas de replicar pods 
y manejar operaciones de los nodos
- `api server` interfaz para interactuar con el cluster, api tipo rest, se utiliza el `kubectl`

## Arquitectura de un nodo worker

Dentro de un nodo worker, hay distintas capas o componentes. 

Nodo 1 (hardware):

- `networking` cada nodo tiene su interfaz de red
- `kubelet` agente, monitoriza que los contenedores corren dentro de un pod
- `container runtime` puede ser un contenedor de docker o de podman
- `operator system` normalmente es un sistema operativo linux

Algunas notas:

- Un cluster de kubernetes está pensado para soportar cientos o miles de aplicaciones de una organización
- Se pueden tener tantos nodos como sean necesarios
- Se añaden nodos en función de la demanda
- Las aplicaciones se despliegan en el cluster de kubernetes
- Cada aplicación se puede aislar en diferentes nodos, aislar por red, políticas de seguridad

## Tipos de cluster de kubernetes

- `On-premise` Infraestructura propia de la empresa
- `Gestionados` En la nube de un provedor cloud (aws, azure, gcp). Se accede a través de una API

Tipos de cluster de kubernetes on-premise:

- `all in one` se intala todo en un único nodo, el más apropiado para comenzar a entender la tecnología (minikube)
- `single master and multiworker`
- `single master, single etcd and multiworker`
- `multi master and multiworker`
- `multi master, multi etcd and multiworker`

## Infraestructura como código

En kubernetes, todo es un objeto y estos objetos se definen en ficheros `.yaml`

- Las definiciones se guardan y ejecutan el cluster mediante el api server
- Las deficionoes de objetos en kubernetes también se conoce como `IaaC`

Los 6 grandes tipos de objetos:

- `Pods` unidad más pequeña que se puede desplegar y gestionar en kubernetes. Es un grupo de uno o 
más contenedores que comparten almacenamiento, red y especificaciones de cómo ejecutarse. Son efímeros.
- `Deployment` describe el estado deseado de una implementación, ejecuta múltiples réplicas de la 
aplicación, remplaza las que están defectuosas o las que no respoden.
- `Services` definición de cómo exponer una aplicación que se ejecuta en un conjunto de pods como 
un servicio de red (por defecto se usa `round-robin` para balanceo de carga, `toma turnos`)
- `Config Map` permite desacoplar la configuración para hacer que las imagenes sean más portables
- `Labels` pares de clave-valor para organizar, selecionar, consultar y monitorear objetos de forma más eficiente
- `Selectores` mecanismo para hacer consultas a las etiquetas

`Round-Robin` es un algoritmo de planificación de procesos simple de implementar, dentro de un 
sistema operativo se asigna a cada proceso una porción de tiempo equitativa y ordenada, 
tratando a todos los procesos con la misma prioridad.

## Instalación de kubectl

No se recomienda aprender con un cluster de kubernetes gestionado (aws, azure, etc) porque requiere 
mayores conocimientos adicionales (redes, etc)

Pasos:

1. Instalar el cliente `kubectl` para interactuar con el cluster.
Seguir los paso indicados en 
[la página de kubernetes](https://k8s-docs.netlify.app/en/docs/tasks/tools/install-kubectl/)

2. Instalar minikube (se necesita tener docker instalado).
Seguir los paso indicados en 
[la página de minikube](https://minikube.sigs.k8s.io/docs/start/)

Para iniciar el cluster de kubernetes ordenamos:

```bash
minikube start
```

Comprobaciones con:

- `docker ps -a` para comprobar que ha levantado el cluster de kubernetes (como un solo contenedor)
- `minikube status` para ver el estado de kubernetes

Para acceder al dashboard, se puede ordenar:

```bash
minikube dashboard --url
```

y nos devuelve la url para acceder a la interfaz gráfica que permite gestionar el cluster.

Otros comandos de supervivencia:

- `minikube stop` para parar el cluster de kubernetes

## Primeros pasos con minikube

Directorio de la práctica: `~\practices\p101`

Objetivos:

- Crear un `pod` mediante kubectl para desplegar una aplicación en kubernetes
- Crear un `servicio` mediante kubectl para desplegar una aplicación en kubernetes

Punto de partida:

- Un cluster de kubernetes iniciado
- Una imagen de una aplicación dockerizada (la obtenemos de Docker Hub)

Para crear el pod, ordenamos:

```bash
kubectl run kbillingapp --image=sotobotero/udemy-devops:0.0.1 --port=80 80
```

Comprobaciones sin utilizar el dashboard:

- `kubectl get pods` para consultar los pods creados
- `kubectl describe pod kbillingapp` para consultar toda la información acerca del pod (ip, puertos)

Si intentamos navegar a `172.17.0.5:80` no ocurre nada, porque el pod no está expuesto como un servicio. 
 
Existen distintos tipos de servicios de exposición. Kubernetes da soporte a cuatro tipos básicos de 
servicios de red: `ClusterIP`, `NodePort`, `LoadBalancer` e `Ingress`. 

- Los servicios de `ClusterIP` hacen que sus apps sean accesibles internamente para permitir la
 comunicación entre los pods de su clúster. 
- Los servicios de `NodePort`, `LoadBalancer` e `Ingress` hacen que las apps sean accesibles 
externamente desde el Internet público o desde una red privada.

De momento vamos a ver el más sencillo, para ello se ordena:

```bash
kubectl expose pod kbillingapp --type=LoadBalancer --port=8080 --target-port=80
```

donde `kbillingapp` es el nombre del pod, `8080` es el puerto que queremos exponer y `80` es el 
puerto destino (del pod) dentro del cluster. 

Para consultar el puerto destino del cluster utilizamos 

```bash
minikube describe pod kbillingapp
```

Para comprobar que hemos creado el servicio, se ordena:

```bash
kubectl get services
```

y obtenemos algo como lo siguiente:


|NAME        | TYPE          | EXTERNAL-IP   | PORT(S)        |  AGE   |
|------------|---------------|---------------|----------------|--------|
|kbillingapp | LoadBalancer  | `<pending>`   | 8080:30776/TCP |  9s    |
|kubernetes  | ClusterIP     | `<none>`      |  443/TCP       |  46m   |        


donde `kubernetes` es el servicio por defecto y `kbillingapp` es el servicio que acabamos de crear:

- `8080` es un puerto definido por nosotros
- `30776` es un puerto que ha asignado internamente kubernetes


Para consultar los detalles del servicio ordenemos:

```bash
kubectl describe service kbillingapp
```

Para acceder al servicio, podemos utilizar el siguiente comando de minikube

```bash
minikube service --url kbillingapp
```

que nos devuelve una url, a la que accedemos con un navegador.

# Orquestación de aplicaciones con kubernetes

## Workspace

Directorio de la práctica: `~\practices\p102`

```yaml
├── devops
│   ├── configmap-postgres-initbd.yaml
│   ├── deployment-pgadmin.yaml
│   ├── deployment-postgres.yaml
│   ├── persistence-volume-claim.yaml
│   ├── persistence-volume.yaml
│   ├── secret-dev.yaml
│   ├── secret-pgadmin.yaml
│   ├── service-pgadmin.yaml
│   └── service-postgres.yaml
└── README.md
```

Objetivo de la práctica: realizar un despliegue de un servicio que consta
de un motor de base de datos (postgres) y un administrador de base de
datos (pgadmin).

## Conceptos que hay que tener claros

- Un `cluster de kubernetes` es un conjunto de nodos ó máquinas que gestionan
y ejecutan aplicaciones conteinerizadas.
- Un nodo `worker` es un nodo donde se ejecuta una aplicación conteinerizada. 
- Existe un `control plane` que se encarga de gestionar el cluster de kubernetes. 
En ocasiones se utiliza el termino ` nodos master` para referirnos a lo mismo. Se 
trata de un conjunto de máquinas que gestionan el cluster.
- Una `workload` es una apliación que se ejecuta en el cluster de kubernetes.
- Un `pod` es un conjunto de contenedores corriendo en un nodo worker. Es la 
unidad mínima que se puede crear y gestionar en kubernetes.

Tambíen es bueno recordar que `minikube` esta pensado para crear un cluster 
de un solo nodo y es lo que normalmene se utiliza para aprender kubernetes.

## Comandos útiles antes de comenzar

Para comprobar los distintos `apiVersion` que se pueden utilizar en los 
archivos `.yaml` se puede ordenar

```bash
kubectl api-versions
```

Para consultar los tipos de objetos de kubernetes (me refiero a los
distintos `kind`) que se pueden definir en un archivo `.yaml` se puede ordenar

```bash
kubectl api-resources
``` 

## Estrcutura de los archivos de yaml

Los campos que más aparecen son `metadata` y `spec` que en ocasiones
están anidados a un campo (p.e. `template`) que cuelga de un `spec` de
de mayor nivel jerárquico.


## Encriptar las variables de entorno sensibles

Podemos utilizar una terminal bash para cifrar un string, 
por ejemplo `ordenador`. Para cifrarlo(encode) se ordena

```bash
echo -n ordenador | base64
```

y nos devuelve `b3JkZW5hZG9y`. La opción `-n` se
utiliza para que el cifrado no absorba el salto de línea 
que tiene por defecto el comando `echo`.

Para descifrar el string se ordena

```bash
echo c3RyaW5n | base64 -d
```

y nos devuelve `ordenador` (sin salto de línea).

Observar que este cifrado solo se utiliza para evitar
que lo pueda ver el ojo humano.

### Archivos

Notas:

- El objeto `Secret` permite almacenanar variables de entorno sensibles.
- Las variables de entorno se consultan habitualmente en Docker Hub.
- El valor `Opaque` indica que el campo `data` es no estructurado, es decir, que 
puede contener pares clave-valor arbitrarios.

Archivo `secret-dev.yaml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  labels:
    app: postgres
#meant that we can use arbitrary key pair values
type: Opaque
data:
  POSTGRES_DB: cG9zdGdyZXM=
  POSTGRES_USER: cG9zdGdyZXM=
  POSTGRES_PASSWORD: cXdlcnR5
```

Archivo `secret-postgres.yaml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: pgadmin-secret
  labels:
    app: postgres
type: Opaque
data:
  PGADMIN_DEFAULT_EMAIL: YWRtaW5AYWRtaW4uY29t
  PGADMIN_DEFAULT_PASSWORD: cXdlcnR5
  PGADMIN_PORT: ODA=
```

## Configurar y definir la persistencia

Notas:

- El objeto `PersistentVolume` define la capa de almacenamiento. Es independiente del
ciclo de vida de los pods. Se encarga de preservar los datos. 
- Utilizamos almacenamiento `local`.
- Se crea de manera `manual`
- El acceso es `ReadWriteMany`: lectura, escritura, puede ser montado por varios nodos
- El volumen es de tipo `hostPath`, lo que quiere decir que monta un archivo o directorio
(en este caso `/mnt/data`) del filesystem del nodo (host) correspondiente en el pod.

> Warning: HostPath volumes present many security risks, and it is a 
best practice to avoid the use of HostPaths when possible. When a 
HostPath volume must be used, it should be scoped to only the 
required file or directory, and mounted as ReadOnly. 

Consultar lo anterior en la documentación oficial.

Archivo `persistence-volume.yaml`:

```yaml
kind: PersistentVolume
apiVersion: v1 
metadata:
  name: postgres-volume
  labels:
    type: local
    app: postgres
spec:
  storageClassName: manual
  capacity:
    storage: 5Gi
  accessModes:
    - ReadWriteMany
  hostPath:
    path: "/mnt/data/"
```

Notas:

- El objeto `PersistentVolumeClaim` (PVC) se utiliza como la solicitud de almacenamiento (PV) por el pod (aplicación)
- La petición es de 2Gi para postgres

Archivo `persistence-volume-claim.yaml`:

```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: postgres-claim
  labels:
    app: postgres
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 2Gi
```

## Otras configuraciones. Config Map

Notas:

- El objeto `ConfigMap` permite inyectar datos de configuración
en los pods. Puede tener múltiples usos.
- En este caso se utiliza para inicializar la base de datos: crear usuario, crear base
de datos y otorgar al usuario todos los privilegios.
- Los datos almacenados en un `ConfigMap` pueden ser referenciados en un volumen
de tipo `configMap` y luego ser usado por las aplicaciones que se ejecutan en el pod.
Consultar el archivo `deployment-postgres.yaml`.

Archivo `configmap-postgres-initbd.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-init-script-configmap
data:
  initdb.sh: |-
   #!/bin/bash
   set -e
   psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER billingapp WITH PASSWORD 'qwerty';
    CREATE DATABASE billingapp_db;
    GRANT ALL PRIVILEGES ON DATABASE billingapp_db TO billingapp;
   EOSQL
```

## Definir el estado deseado. Deployments

Notas:

- El objeto `Deployment` permite definir el despliegue o el estado deseado.
- El campo `spec.replicas` define el número de réplicas de los pods
- El campo `spec.selector` define como el Deployment identifica los pods que gestiona.
- El campo `spec.template` define las especicaciones para crear los pods. Incluye los
campos `spec.template.spec.containers` y `spec.template.spec.volumes`.

El campo `spec.template.spec.containers`:

- Definición de contenedores (imagen, puertos, puntos montaje).
- Las images son del repositorio de Docker Hub.
- El par `imagePullPolicy: IfNotPresent` indicar que la descargue si no lo ha hecho anteriormente.
- El campo `.envFrom` recoge las variables de entorno.
- El campo `.volumeMounts` recoge las rutas dentro del contenedor donde se montan los volúmenes. 
Se utilizan los nombre de los voúmenes definidos en el campo `spec.template.spec.volumes`.

El campo `spec.template.spec.volumes` :

- Asignación de los volumenes definidos en `PersistenceVolume`
- Los datos almacenados en un `ConfigMap` pueden ser referenciados en un volumen
de tipo `configMap` y luego ser usado por las aplicaciones que se ejecutan en el pod.
Es lo que se hace en `deployment-postgres.yaml`.


Archivo `deployment-postgres.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres-deployment
  labels:
    app: postgres
spec:
  replicas: 1
  selector: 
    matchLabels:
     app: postgres
  template:
    metadata:
      labels:
        app: postgres       
    spec:
      containers:
        - name: postgres
          image: postgres:latest
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 5432
          envFrom:
            - secretRef:
                name: postgres-secret
          volumeMounts:
            - mountPath: /var/lib/postgresql/data
              name: postgredb
            - mountPath: /docker-entrypoint-initdb.d
              name : init-script
      volumes:
        - name: postgredb
          persistentVolumeClaim:
            claimName: postgres-claim
        - name: init-script
          configMap:
             name: postgres-init-script-configmap
```

Archivo `deployment-pgadmin`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pgadmin-deployment
spec:
  selector:
   matchLabels:
    app: pgadmin
  replicas: 1
  template:
    metadata:
      labels:
        app: pgadmin
    spec:
      containers:
        - name: pgadmin4
          image: dpage/pgadmin4        
          envFrom:
            - secretRef:
                name: pgadmin-secret
          ports:
            - containerPort: 80
              name: pgadminport
```

## Definir los servicios para exponer los pods al exterior

Notas:

- El objeto `Service` permite definir el servicio para exponer el pod al exterior.
- En un entorno real utilizaríamos un servicio de tipo `LoadBalancer`.
- Aquí se define un servicio de tipo `NodePort`. 
- Para el `postgres-service` el puerto interno y externo es `5432` y `30432` respectivamente.
- El puerto interno es el del pod.
- El puerto externo se expone en el cluster de kubernetes.

Archivo `service-postgres.yaml`:

```yaml
kind: Service
apiVersion: v1
metadata:
  name: postgres-service
  labels:
    app: postgres
spec:  
  ports:
  - name: postgres
    port: 5432
    nodePort : 30432 
  #type: LoadBalancer
  type: NodePort
  selector:
   app: postgres
```

Archivo `service-pgadmin.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: pgadmin-service
  labels:
    app: pgadmin
spec:
  selector:
   app: pgadmin
  type: NodePort
  ports:
   - port: 80
     nodePort: 30200
```

## Crear los objetos en el cluster

El cluster de kuberntes tiene que estar iniciado. Comprobarlo
con un `minikube status`

Ejecutamos los siguientes comandos:

```bash
kubectl apply -f secret-dev.yaml 
kubectl apply -f secret-pgadmin.yaml 
kubectl apply -f configmap-postgres-initbd.yaml 
kubectl apply -f persistence-volume.yaml 
kubectl apply -f persistence-volume-claim.yaml 
kubectl apply -f deployment-postgres.yaml 
kubectl apply -f deployment-pgadmin.yaml 
kubectl apply -f secret-dev.yaml 
kubectl apply -f deployment-postgres.yaml 
kubectl apply -f service-postgres.yaml 
kubectl apply -f service-pgadmin.yaml 
```

Se puede ir comprobando en el dashboarad (`minikube dashboard`) que todo
se crea correctamente:

- Consultar los logs de los pods si hay alguno problema.
- A vecés hay que borrar el deployment y volver a crearlo para resolver algún problema.

## Probar el servicio

Necesitamos la `<ip del cluster>` de kubernetes:

```bash
minikube ip
```

Sin utilizar el dashboard, la información del cluster (pods, servicios, puertos, etc)
la obtenemos ordenando

```bash
kubectl get all
``` 

Navegamos a `<ip del cluster>:<puerto externo pgadmin-service>`

- Se carga la interfaz gráfica del servicio
- Se utilizan las credenciales definidas en `secret-pgadmin.yaml`

### Opción 1. Desde la interfaz pgAdmin

Pasos para conectar el servidor. 

`Add New Server`:

- Name `postgresService`
- Host name/adressess `<ip del cluster>` 
- Port `<puerto externo postgres-service>`
- Database, Username, Pasword: definidas en `secret-postgres.yaml`

### Opción 2. Desde la interfaz pgAdmin

Pasos para conectar el servidor. 

`Add New Server`:

- Name `postgresPod`
- Host name/adressess `<postgres-service CLUSTER-IP>` 
- Port `<puerto interno postgres-service>`
- Database, Username, Pasword: definidas en `configmap-postgres-initbd.yaml`

## Navegar a la localización de los volúmenes

Se recuerda que minikube esta levantado como un contenedor de docker, y ese contenedor
es el único nodo de nuestro clúster. Por tanto para localizar el volumen creado, hay que 
hacer ordenar

```bash
docker exec -ti minikube /bin/bash
```

donde `minikube` es el nombre del contenedor donde se encuentra el nodo único 
de kubernetes. Una vez dentro, podemos navegar a la localización del volumen
y listar los archivos que contiene:

```bash
ls -lah /mnt/data
```

## Eliminar y recrear la orquestación del servicio 

Para eliminar la orquestación llevada a cabo en las secciones anteriores
podemos ejecutar desde la carpeta donde se ecuentrean los archivos `.yaml`:

```bash
kubectl delete -f ./
``` 

y para ejecutar la orquestación de nuevo, se ordena

```bash
kubectl apply -f ./
``` 

## Orquestación del servicio de facturación

## Workspace

Directorio de la práctica: `~\practices\p103`

```yaml
.
├── billingApp
│   ├── deployment-billing-app-back.yaml
│   ├── deployment-billing-app-front.yaml
│   ├── service-billingapp-back.yaml
│   └── service-billingapp-front.yaml
├── billingApp_images_v4
│   ├── angular
│   │   ├── billingApp
│   │   ├── Dockerfile
│   │   └── nginx.conf
│   └── java
│       ├── billing-0.0.4-SNAPSHOT.jar
│       └── Dockerfile
├── db
│   ├── configmap-postgres-initbd.yaml
│   ├── deployment-pgadmin.yaml
│   ├── deployment-postgres.yaml
│   ├── persistence-volume-claim.yaml
│   ├── persistence-volume.yaml
│   ├── secret-dev.yaml
│   ├── secret-pgadmin.yaml
│   ├── service-pgadmin.yaml
│   └── service-postgres.yaml
└── README.md

```

Objetivo de la práctica: realizar un despliegue de un servicio 
de facturación que utiliza una base de datos postgres.

## Configuración para crear imágenes locales dentro del cluster de kubernetes

Queremos crear imágenes locales dentro del cluster de kubernetes 
(`docker image ls` dentro del contenedor de minikube) que luego van a se llamadas
desde el `Deployment` correspondiente. 

Para ello es necesario ejecutar los siguiente comandos desde la terminal 
local (desde fuera del contenedor de minikube)

Se ordena

```bash
minikube docker-env
```

y se muestra la siguiente salida

```bash
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.49.2:2376"
export DOCKER_CERT_PATH="/home/roberto/.minikube/certs"
export MINIKUBE_ACTIVE_DOCKERD="minikube"

# To point your shell to minikube's docker-daemon, run:
# eval $(minikube -p minikube docker-env)
```

A continuación de ordena

```bash
eval $(minikube -p minikube docker-env)
```

**Nota importante** Lo anterior solo tiene efecto en la terminal en la que se ejecuta.
Si se sale, ya no tiene efecto.

A continuación se crean las imágenes de las aplicaciones del back (java)
y del front (angular) con los comandos habituales (hay que situarse en
los directorios del Dockerfile se se indica ` .`)

Para consturir la imagen del microservicio java se ordena

```bash
docker build -t billingapp-back:0.0.4 --no-cache --build-arg JAR_FILE=./*.jar .
```

Para construir la imagen de angular, se ordena:

```bash
docker build -t billingapp-front:0.0.4 --no-cache .
```

Para hacer la comprobación nos metemos en el cluster ejecutando

```bash
minikube ssh
```

y dentro del contenedor de minikube al que acabamos de acceder
(es un cluster de un único nodo situado en un contenedor de docker)
ejecutamos un `docker image ls` para verificar que se han contruído
las imágenes.

A continuación ejecutamos todos los archivos `.yaml` de la
orquestación ejecutando desde el directorio donde se encuentran

```bash
kubectl apply ./
```

Para monitorizar la orquestación podemos utilizar alguno de los dos
comandos siguiente:

- `kubectl get all` para monitorizar desde la terminal
- `minikube dashboard` para monitorizar con el dashboard desde un navegador

Podemos probar a eliminar un pod y ver como se crea uno nuevo. Podemos hacerlo
desde el dashboard o ejecutando desde la terminal

```bash
kubectl delete pod <nombre del pod>
```

## Navegación

### Front en angular

La url es

- [192.168.49.2:30100/](192.168.49.2:30100)

donde la ip es la del cluster de kubernetes (se puede obtener ejecutando `minikube ip`) y el puerto es el
puerto interno del servicio del front.

### Back en microservicio java

La url es

- [192.168.49.2:30780/swagger-ui/index.html/swagger-ui/index.html](192.168.49.2:30780/swagger-ui/index.html/swagger-ui/index.html)

idem anterior el puerto es el puerto interno del servicio del back.

### Administrador de la base de datos

La url es

- [192.168.49.2:30200](192.168.49.2:30200)

idem anterior el puerto es el puerto interno del servicio del admin.

Desde aqui dentro consultamos las bases de datos del postgres.
Hay que conectar el servidor con la siguiente información:

Host address: `192.168.49.2` ip cluster de kubernetes
Port: `30432` puerto interno del servicio de postgres
Username: `postgres`
Password: `qwerty`

## Algunos problemas

La orquestación se ejecuta sin fallo, se crean y se ejecutan los pods correctamente 
pero no se conecta el front con el back.

- Puede ser un problema de permisos y creación de volúmenes

# Gestión de repositorios y control de versiones con git

## Conceptos de Git

Git es un sistema de gestión de versiones. Se puede integrar con diferentes 
repositorios.

Los repositorios más utilizados son:

- `Bitbucket`
- `GitLab`
- `GitHub`

Las dos técnicas más utilizadas para trabajar con git:

- `Git trunk Based` se mantiene una única rama a lo largo del tiempo y se intregan en ella las ramas secundarias.
- `Git Flow` estilo más complejo, típicamente tiene 5 ramas.

Conceptos:

- `Trunk/master/main` es la rama principal del repositorio.
- `Branch` es una nueva bifurcación del estado actual del código.
- `Fork` es un clon de todo el repositorio y del historial de los cambios.
- `Tag` es una etiqueta para indentificar las versiones del producto.

## Estilo de trabajo trunk-based

En castellano, desarrollo basado en troncos.

Consultar esta documentación sobre 
[trunk-based development](https://www.atlassian.com/es/continuous-delivery/continuous-integration/trunk-based-development).

Una ramas principal `master/main`. Parten ramas secundarias que se integran en la principal.

## Estilo de trabajo Gitflow y conclusiones

Consultar esta documentación sobre 
[el flujo de trabajo de Gitflow](https://www.atlassian.com/es/git/tutorials/comparing-workflows/gitflow-workflow).

Tipos de ramas:

- Dos ramas principales: `master/main` y `develop`. Nunca reciben código directo.
- Tres tipos de ramas secundarias: `features`, `release`, `hotfix`.
- La rama `master` (master) almacena el historial de versiones oficiales. 
- La rama `develop` (develop) es la rama de integración de las ramas features.
- La rama `feature` (feature/myfeature) se utiliza por los desarrolladores 
para agregar código que añade nuevas características. Parte de develop. Es temporal.
- La rama `realease` (realease-x.y.z) se utiliza en el entorno de pruebas. 
Parte de develop. Se intrega en las dos ramas pricipales.
- La rama `hotfix` (hotfix-x.y.z) se utiliza para arreglar errores urgentes. 
Parte de develop.Se intrega en las dos ramas pricipales.

Conclusiones:

- El estilo trunk-based es el estándar usado para equipos de ingeniería de 
lto rendimiento (integración continua, iterar rápido).
- El estilo gitflow es adecuado cuando existe un calendario de release fijo.

## Instalar git y crear repositorio en github o gitlab

Instalación y configuración inicial:

- Hay que tener git instalado. Comprobar con el comando `git --version`. Si no se tiene, instalar.
- Hay que tener una cuenta en GitHub


Hay que configurar el usuario/correo (puede ser inventado) con la orden

```bash
git config --global user.name <name>
git config --global user.email <email>
```

Verificar lo anterior con `git config --list` y para salir teclear `q`.

## Método de autenticación en github con token

En Agosto de 2021 Microsoft cambio la política de autenticación. Ahora hay 
que autenticarse con un token de uso personal.

Pasos a seguir para generar un token de acceso personal en github:

- En GitHub, navegar a `Settings - Developer settings - Personal access tokens`.
- Generar el token dotándole de un nombre y una fecha de caducidad.
- Si no somos expertos, seleccionamos todos los `scopes` para no tener ningún 
error de lectura o escritura.

Cuando nos pidan una contraseña en la terminal, introduciremos el usuario y el token.

En `ubuntu` utilizamos el archivo `~/USER_HOME/.netrc`

## Primeros pasos con git y gihub

Directorio de la práctica: `~\practices\p201`   

Notas iniciales:

- Repasar los apuntes del curso de git específico.
- Si hay algún error con el CRLF, ejecutar `git config core.autocrlf true`.

Se pude empezar creando el repositorio remoto en GitHub, que tendrá el nombre
`<user name>/<repository name>`.

### Día 1 del desarrollador 1

Se parte con un único archivo `README.md` en el espacio de trabajo.

Se hace un commit del `main` local:

- `git init` para iniciar el control de versiones
- `git add .` o `git add README.md` para subir el archivo al stage
- Verificar con `git status`
- `git commit -m "Readme creado"` para hacer el commit
- Verificar con `git log`
- `git branch` o `git branch -M main` para ver/crear la rama actual (*) 

Cambiar el nombre de la rama `master` a `main` es opcional, pero es importante
mantener la coherencia con los nombres.

Se sube el `main` local al `main` remoto:

```bash
git remote add origin https://github.com/rhodevops/20220211curso_201.git
git push -u origin main
```

El primer comando añade el repositorio remoto que hemos creado asignándo a su
`url` el alias `origin`. Este alias se utilizará en otros comandos de git.

El segundo comando sube el contenido de la rama local actual
al repositorio remoro (`origin`), a la rama `main`. 

La opción `-u` (de `upstream`) se utiliza para configurar ese repositorio como
el principal (útil cuando tenemos varios repositorios remotos). 

Si solo indicamos `git push main`, el push se realiza hacía el repositorio 
establecido como principal.

**NOTA IMPORTATE** Se modifica el `main` remoto. Modificamos el archivo `readme` y guardamos los cambios con 
un commit, simulando el trabajo del desarrollador 2.

Se crea una rama `feature/rn-01` local que sale del `main` local:

- `git branch feature/rn-01 main` para crear una nueva ramma que parte de `main`
- `git checkout <rama>` para cambiar a la rama indicada

El nombre de la rama creada hace referencia a una característica nueva, las iniciales del 
desarrollador y número identificativo. Verificar los pasos anteriores con `git branch`.

En el repositorio local, agregamos el código fuente del microservicio `billingApp` 
al repositorio local y hacemos un push al repositorio remoto ordenando

```
git push -u origin feature/rn-01
``` 

Es importante tener claro que acabamos el día estando desincronizados 
respecto al cambio que hicimos en el repositorio remoto. 

### Día 1 del desarrollador 2

Queda simulado con el cambio que hicimos en el readme del repositorio remoto. En
la practica este desarrollador ha trabajado sobe su rama, y se han aprobado e
integrado los cambios en el main `remoto`. 

### Día 2 del desarrollador 1

A continuación, simulamos otro día de trabajo. 

Hay que comenzar actualizando la rama sobre la que se trabaja, puesto que el
día anterior nos hemos desincronizado con otros desarrolladores. 

Se dan dos pasos:

1. Actualizar nuestra rama `feature/rn-01` local con el `main` remoto mediante un `pull`
2. Hacer `commit` de los cambios
3. Subir los cambios a nuestra rama `feature/rn-01` remota mediante un `push`

Nos situamos en la rama `feature/rn-01` y ordenamos

```bash
git pull origin main
``` 

Así, se realiza un merge de la rama `main` remota hacia la rama `feature/rn-01` local.

El `log` y el `status` tras el pull:

```bash
# log tras pull y commit
commit 08fb84dc8f3b7b82b4a145f7a5f199df8350e8fb (HEAD -> feature/rn-01)
Merge: df0256e 9d37fdc
Author: rhodevops <rhodevops@gmail.com>
Date:   Mon Jun 6 17:13:43 2022 +0200

    Merge branch 'main' of https://github.com/rhodevops/20220211curso_201 into feature/rn-01

commit df0256e594dbc82889735f1f6e1274d6d0ab2eab (origin/feature/rn-01)
Author: rhodevops <rhodevops@gmail.com>
Date:   Mon Jun 6 16:50:06 2022 +0200

    microservicio añadido
``` 

```bash
# status tras pull y commit
On branch feature/rn-01
Your branch is ahead of 'origin/feature/rn-01' by 2 commits.
  (use "git push" to publish your local commits)

nothing to commit, working tree clean
```

Tras esto subimos los cambios al stage (`add`), hacemos un commit y subimos los cambios 
a la rama `feature/rn-000123` ordenando

```
git push -u origin feature/rn-01
``` 

El `log` y el `status` tras el push:

```bash
# log tras push
commit 08fb84dc8f3b7b82b4a145f7a5f199df8350e8fb (HEAD -> feature/rn-01, origin/feature/rn-01)
Merge: df0256e 9d37fdc
Author: rhodevops <rhodevops@gmail.com>
Date:   Mon Jun 6 17:13:43 2022 +0200

    Merge branch 'main' of https://github.com/rhodevops/20220211curso_201 into feature/rn-01

commit df0256e594dbc82889735f1f6e1274d6d0ab2eab
Author: rhodevops <rhodevops@gmail.com>
Date:   Mon Jun 6 16:50:06 2022 +0200

    microservicio añadido

commit 9d37fdc6d08c4fcc4ac59f1acbf7ae78e7de7db0 (origin/main)
```

```bash
# status tras push
On branch feature/rn-01
Your branch is up to date with 'origin/feature/rn-01'.

nothing to commit, working tree clean
```

Observar como varía el puntero `HEAD`.

Dos objetivos cumplidos:

- Tenemos nuestra rama de trabajo local sicronizada
- No hemos enviado cambios directos a la rama main remota

### Integración del código del desarrollador 1

Ahora se resolvería lo siguiente:

1. El desarrollador 1 crea una petición, `Pull request`, para que su código se integre.
2. Un responsable de la integración del código gestiona el pull request.

`Create pull request` por el desarrollador 1:

- En GitHub, se selecciona `Compare & pull request`. 
- Solicitud para integrar la rama `feature/rn-000123` con la rama `main`.
- Se selecciona `Create pull request` tras agregar un mensaje descriptivo

![Github. Create pull request](./imag/github-Create-pull-request.png)
 
Tras esto, GitHub nos da la opción de hacer el `merge`. Sin embargo, en un
entorno de trabajo, en general no es el desarrollador 1 el que hará el merge.
Su trabajo termina con la petición.

**Una nota importante** Antes de hacer el `Merge pull request` se puede establecer la
siguiente confioguración. Se navega a los `settings` del repositorio y activamos 
la opción `Automatically delete head branches`. Lo que hace esto es eliminar de forma
automática, tras hacer el `Merge pull request`, la rama que se ha integrado en el
`main` (o en cualquier otra rama).

Gestión del `pull request` por el responsable de la integración:

- En GitHub, se accede a la pestaña `Pull requests`
- Se selecciona el pull request abierto: `Feature/rn 01`
- Se pude optar por tres acciones: `Merge pull request`/`Close pull request`/`Comment`

Normalmente, si todo está bien, se opta un `Merge pull request`:

- Se añade un mensaje (opcional) y se confirma, `Confirm merge`.

Dependiendo de la configuración, la rama integrada persiste o no. En caso de que persista, se
puede entrar en un `pull request` cerrado y selecionar manualmente `Delete branch` para eliminar
la rama integrada.

Observaiones:

- En este punto y independientemente de que se haya eliminado o no la versión remota de la rama integrada,
esta conitinua existiendo en el repositorio local. 
- Lo ideal es automatizar este tipo de cosas (integración continua).

### Día x del desarrollador 3: clon

#### Lo que no se tiene que hacer

Se parte con un único archivo `README.md` en el espacio de trabajo y se
hace lo siguiente:

```bash
git init
git add .
git commit -m "primer commit"
git remote add origin https://github.com/rhodevops/20220211curso_201.git
git branch -M main
git branch feature/test-01 main
git push -u origin feature/test-01
```

Esta aproximación para colaborar en el repositorio no es correcta y da
problemas en el momento de crear un `Pull request` en GitHub:

> There isn’t anything to compare.
main and feature/test-01 are entirely different commit histories. 

o al intentar hacer un `git pull origin main`:

```bash
From https://github.com/rhodevops/20220211curso_201
 * branch            main       -> FETCH_HEAD
fatal: refusing to merge unrelated histories
```

#### Hacer un clon o un fork

Se debe de empezar la colaboración haciendo un fork.

Diferencias entre `clon` y `fork`:

- Si haces un `clon` normal de un repositorio, el espacio en GitHub de ese clon seguirá asociado 
al repositorio que has clonado. 
- Si clonas un repositorio que era tuyo, podrás realizar cambios en local y subirlos a GitHub siempre que quieras. 
- Si clonas un repositorio de otro desarrollador (y no tienes permisos de escritura sobre él) entonces no podrás 
subir cambios, porque GitHub no te lo permitirá. Para este caso es donde necesitas un fork.
- Un fork es una copia de un repositorio, pero creado en tu propia cuenta de GitHub, donde sí que tienes permisos de escritura. 

Por tanto, si tienes intención de bajarte un repositorio de GitHub para hacer cambios en él y ese repositorio no te pertenece, 
lo más normal es que crees un fork primero y luego clones en local tu propio fork.

Se parte con un directorio vacío:

En la terminal:

```bash
git clone https://github.com/rhodevops/20220211curso_201.git
# se crea el directorio 20220211curso_201/
cd 20220211curso_201/
git branch feature/rl-01
git checkout feature/rl-01 
echo Esto es una prueba realizada por el desarrollador 2 >> test.txt
git add .
git commit -m "test.txt añadido"
git push -u origin feature/rl-01 
```

En GiHub:

- Se crea una `Pull request` para integrar la rama `feature/rl-01` en la rama `main`
- Se integra haciendo un `Merge pull request`

Investigar como se haría un fork.

## Crear un repositorio con el estilo gitflow

[¡PENDIENTE!](url-tapadera)

Notas:

- Recordar que dentro de las prácticas devops se recomienda la integración continua y 
por tanto la estrategia `trunk-based`.
- Consultar el video del curso. Se proponer una práctica para aplicar la técnica gitflow.
- En Windows, hay que descargar e instalar git-flow. Después de instalar git-flow, se 
puede utilizar en el proyecto ejecutando `git flow init` en lugar de `git init`. 

## Integración continua (CI) Jenkins pipelines

Una de las ideas fuerza de la práctica DevOps es la automatización de los procesos de 
desarrollo y entrega de un producto con el fin de dar un mejor servicio al cliente.

La `integración continua` (continuous integration) es fundamental en la práctica DevOps. 
Junto a este concepto, tenemos los conceptos de `entrega continua` (continuous delivery) 
y `despliegue continuo` (continuous deployment).

Notas de nomenclatura:

- Se usan indistintamente los nombre de entrega continua y distribución continua. 
- Se usan indistintamente los nombre de despliegue continuo, implementación continua o 
liberación continua. 

Algunas fuentes interesantes para leer acerca de la CI/CD son las siguientes:

- [`RedHat`](https://www.redhat.com/es/topics/devops/what-is-ci-cd)
- [`Atlassian`](https://www.atlassian.com/es/continuous-delivery/principles/continuous-integration-vs-delivery-vs-deployment)
- [`JetBrains`](https://www.jetbrains.com/es-es/teamcity/ci-cd-guide/continuous-integration-vs-delivery-vs-deployment/)

Las tres fases:

- La `integración continua` (CI) es el proceso de automatización para los desarrolladores 
(integrar los nuevos cambios en el código de cada desarrollador).
- La `entrega continua` y `despliegue continuo` (CD) es el proceso de automatización de 
las etapas posteriores del desarrollo (entorno de pruebas y entrega al cliente final).

Principales herramientas utilizadas:

- `Pipeline` Es el conjunto de procesos automáticos y herramientas utilizados para hacer 
una tarea, en este contexto para llevar a cabo la CI/CD.
- `Jenkins` Herramientas (opensource) de automatización.
- `Slack` Es una plataforma de comunicación empresarial. Tiene canales para notificar tareas.
- `SonarQube` Herramienta (opensource) de inspección del código.
- `Selenium` Es un framework para probar aplicaciones web.

## Jenkins. Instalación del servidor de integración continua

El servidor va a constar de `Jenkins` y `Maven`. Dos formas de instalarlo:

- Manualmente, desde la la página de Jenkins. Aquí los enlaces de la documentación oficial 
de [Jenkins](https://www.jenkins.io/download/) y [Blue Ocean](https://www.jenkins.io/projects/blueocean/). 
Blue Ocean es una interfaz amigable para Jenkins.
- Utilizando una imagen de Docker personalizada que contiene Jenkins y Maven (autor del curso).
- Utilizando la [Official Jenkins Docker image](https://github.com/jenkinsci/docker/blob/master/README.md)

Utilizo la tercera opción, sin necesidad de utilizar un Dockerfile. Esto tiene una implicación,
hay que configurar maven en Jenkins.

El comando `docker run` que levanta el contenedor de jenkins nos da un hash, que deberemos utilizar cuando
naveguemos a `localhost:8080`. Instalamos los plugins recomendados y
seguimos los pasos indicados.

Si perdemos el hash, podemos encontrarlo leyendo los logs del contenedor
levantado:

```bash
docker logs <container>
```

## ¿Qué es una pipiline?

Un `pipeline` es una secuencia de operaciones automatizadas que usualmente representa una parte de la entrega 
y el aseguramiento de la calidad del software. Podemos verlo simplemente como una secuencia de scripts 
que provee algunos beneficios como `Agrupación de operaciones`, en etapas que llamaremos stages;
`visibilidad`, ayuda a un análisis del fallo rápido; y `retroalimentación`, os miembros del equipo se dan 
cuenta de los problemas tan pronto como ocurren.

Jenkins permite crear pipelines para hacer posible la integración continua.
(`Aquí`)[https://www.jenkins.io/doc/book/pipeline/getting-started/] un tutorial de Jenkins.

## Crear una pipeline de estilo libre con maven y github

Directorio de la práctica: `~\practices\p302`

Punto de partida:

- Repositorio GitHub con el código fuente de `billing` y el archivo `README.md`.

Configuración previa de maven. Navegamos a `Global tool configuration` y añadimos
un `maven`, en mi caso, con la siguiente configuración:

- Install automatically
- Install from Apache Version 3.8.5

Creamos un `Freestyle Project` con la siguiente configuración:

1. En **Source Code Management**.
  - Seleccionamo `git` e introducimos la `url` del repositorio de GitHub (termina en .git)
  - `Add - Jenkins` para agregar usuario y ¿token? de GitHub.
  - En `Branch Specifier` indicamos `*/main*`.
2. En **Buid Triggers**. Seleccionamos `GitHub hook trigger for GITScm polling`.
3. En **Build**. Seleccionamos `Invoke top-level Maven targets` y lo configuramos
para ejecutar un `mvn -f billing/pom.xml clean install`.
  - En `Maven Version`, seleccionamos el que hemos configurado previamente.
  - En `goles` escribimos `clean install`. 
  - En `Avanced`, en `POM` escribimos  `billing/pom.xml` (ruta del repositorio de github).

Tras guardarlo, volvemos al panel principal del proyecto y hacemos un `build now`. 
Podemos hacemos clic en `#1` para consultar el `Console Ouput`, podemos ver lo que se ha hecho:

```bash
[INFO] Installing /var/jenkins_home/workspace/20220211curso_202/billing/target/billing-0.0.1-SNAPSHOT.jar to /var/jenkins_home/.m2/repository/com/paymentchain/billing/0.0.1-SNAPSHOT/billing-0.0.1-SNAPSHOT.jar
[INFO] Installing /var/jenkins_home/workspace/20220211curso_202/billing/pom.xml to /var/jenkins_home/.m2/repository/com/paymentchain/billing/0.0.1-SNAPSHOT/billing-0.0.1-SNAPSHOT.pom
```

También podemos entrar en el contenedor de jenkins

```bash
docker exec -ti <jenkins container> /bin/bash
```

y hacer algunas comprobaciones (la persistencia está en `/var/jenkins_home/`):

```bash
ls  /var/jenkins_home/.m2/repository/com/paymentchain/billing/ -lah
ls  /var/jenkins_home/.m2/repository/com/paymentchain/billing/0.0.1-SNAPSHOT/ -lah
```

Las rutas anteriore se sacan del ouput de la consola que podemos ver en jenkins.

## Algunas notas sobre ngrok

En la práctica anterior, el servidor de integración continua (Jenkins) estaba en el `localhost`. 
Pero ahora necesitamos que sea visible desde internet. Para ello vamos a utilizar `ngrok`.

- La herramienta `ngrok` permite exponer hacia el exterior cualquier servicio web local que 
tengamos en nuestro ordenador, en cualquier puerto (no se usa en producción).
- Queremos una comunicación basada en eventos. Los `webhooks` son eventos que desencadenan 
acciones. 

Algo de (`documentación sobre webhooks`)[https://docs.github.com/en/developers/webhooks-and-events/webhooks/about-webhooks]


## Conectar Jenkins con GitHub, configurar webhooks y ngrok

En esta práctica, vamos a aprender como conectar Jenkins con GitHub.

Directorio de la práctica: `~\practices\p303`

### Crear el repositorio en GitHub

Repositorio GitHub de partida:

- `README.md`
- `angularWorkSpace` aplicación en angular
- `billing` microservicio 

### Intalación y configuración de ngrok

Se siguen los pasos indicados en la web oficial. Una vez que tenemos el
binario `ngrok`, hay que ejecutar

```bash
ngrok config add-authtoken 2AHbOpGylWWNTUudWj6qRfeS6XD_ej7PAkUfbYoAQrXCu5v
```

y el authtoken se guarda en `/USER_HOME/.config/ngrok/ngrok.yml`. 

### Exponer el puerto 8080

Al ordenar

```bash
ngrok http 8080
```

se genera una url del tipo `<https://x-x-x-x-x.eu.ngrok.io>` que es la que vamos
a utilizar en el webhook de GitHub. Esta terminal se deja abierta.

Por cierto, un `webhook` (o `API inversa`) es una herramienta que permite que un sistema 
o aplicación envíe notificaciones sobre un evento específico a otro sistema o aplicación en tiempo real.


### Configurar el webhook en GitHub

Creamos el siguiente webhook en el repositorio remoto de GitHub:

- Payload URL `https://x-x-x-x-x.eu.ngrok.io/github-webhook/`
- Content type `application/json`

En este caso, indicamos que nos de aviso de los `push` que
se lleven a cabo. 

Tras la configuración, en la terminal abierta
del ngrok aparece la petición que se envía de prueba y la respuesta

```bash
POST /github-webhook/          200 OK  
```

Para probar que funciona bien, creamos un nueva rama `feature/addtest` en el repositorio
local y hacemos un `git push -u origin feature/addtest` para hacer el push a GitHub. Tras
esto, debemos recibir un mensaje como el anterior en la terminal del abierta del ngrok.

También se pueden consultar estos mensajes navegando en el repositorio de GitHub hacia
`Settings - Weebhooks - <webhook> - Recent Deliveries`

## Crear una pipeline de jenkins basado en webhooks

### Concectar el servidor de GitHub en Jenkins

Navegamos a `Manage Jenkins - Configure System - GitHub` y seleccionamos
`Add Github Server` con la siguiente configuración:

- Name: `rhodevops server`
- API url: por defecto `https://api.github.com` 
- Credentials: añadir una de tipo `secret text` (hacel test de conexión)

Como `secret` hay que añadir el token de GitHub. Es importante no olvidarse
de seleccionar el tipo de credencial después de crearlo.

### Crear la pipeline en Jenkins

Creamos un proyecto de estilo libre con la siguiente configuración:

1. En **General**. Seleccionamos `GitHub project` e introducimos la `url` del proyecto, que es la 
url del repostitorio sin añadir .git.
2. En **Source Code Management**.
  - Seleccionamo `git` e introducimos la `url` del repositorio de GitHub (termina en .git)
  - `Add - Jenkins` para agregar usuario y token de GitHub.
  - En `Branch Specifier` indicamos `origin/feature**`.
3. En **Buid Triggers**. Seleccionamos `GitHub hook trigger for GITScm polling`.
4. En **Build**. Seleccionamos `Invoke top-level Maven targets` y lo configuramos
para ejecutar un `mvn -f billing/pom.xml clean install`.

Notas:

- Rercordar que hay que indicar el ejecutable (versión) de maven utilizado.
- El doble asterisco de `origin/feature**` es para que reconozca cualquier caracter que venga
a continuación. Con un solo asterisco no se reconocería un slash `/`.

### Modificamos un archivo del repositorio

Modificamos el archivo 

> `./billing/src/test/java/com/paymentchain/billing.BasicApplicationTests.java`

```bash
package com.paymentchain.billing;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicApplicationTests {

	@Test
	public void contextLoads() {
		String message = "default message cambio testi devops";
		// prueba que valida que el mensaje anterior no es nulo
		Assert.assertNotNull(message);
	}

}
```

Añadimos los cambios al stage, hacemos commit y hacemos push. Al hace el push
se lanza la `pipeline` que habíamos configurado en Jenkins. Podemos entrar
para ver el ouput de la consola.

Terminamos la practica haciendo un `Pull Request - Merge` en GitHub
para integrar la rama temporal con la rama main (eliminar la rama temporal).

Para dejar limpio el repositorio local, nos cambiamos a la rama `main`, eliminamos
la rama temporal ejecutando

```bash
git branch -d <nombre de la rama>
```

y hacemos un `git pull` para actualizar la rama `main` del repositorio local.

## Pipeline de automatización continua

En esta práctica, vamos a hacer una pipeline de CI para automatizar los cambios 
en el repositorio remoto. Se automatizan varias cosas.

Directorio de la práctica: `~\practices\p304`

### Crear el repositorio en GitHub

Repositorio GitHub de partida:

- `README.md`
- `angularWorkSpace` aplicación en angular
- `billing` microservicio 

Tanto remota como localmente, partimos de una sola rama `main`. 
Creamos una rama feature y no situamos en ella:

```bash
git branch feature/rn
git checkout feature/rn
```

### Crear la pipeline en Jenkins

Creamos un proyecto de estilo libre con la siguiente configuración:

1. En **General**. Seleccionamos `GitHub project` e introducimos la `url` del proyecto.
2. En **Source Code Management**.
  - Seleccionamo `git` e introducimos la `url` del repositorio de GitHub (termina en .git)
  - `Add - Jenkins` para agregar usuario y token de GitHub.
  - En `Additional Behaviours` añadimos un `custom user name/email addres` y escribimos
  `jenkins` y `<correo ficticio>`
  - En `Branch Specifier` indicamos `origin/feature**`.
3. En **Build**:
  - Seleccionamos `Invoke top-level Maven targets` y lo configuramos
  para ejecutar un `mvn -f billing/pom.xml clean install`.
  - Seleccionamos una shell y ejecutamos l siguiente:
```shell
git branch
git checkout main
# merge hacia la rama main
git merge origin/feature/rn
```
3. En **Post-Build Actions**. En `Git Publisher`, seleccionar `Push Only If Build Succeed push only if build succeeds`
y en `branches` configurar Branch to push `main `y Target remote name `origin`

### Probar la pipeline en Jenkins

Para probar lo anterior, hacemos lo siguiente:

1. Hacer un push desde y hacia la rama `feature/rn`.
2. ? Crear/abrir un `Pull Request` (no resolverlo en GitHub)
3. Ejecutar la pipeline. 

El resultado es que se ejecuta el merge integrándose la rama `feature/rn` en
la rama `main`. Además, la primera se borrará si así lo hemos configurado
en en el `Settings` del repositorio (`General - Automatically delete head branches`)

Probar a hacerlo omitiendo el paso 2 anterior. Se pueder hacer, pero no se
borra la rama `feature/rn`del repositotio remoto.

### Algunos comentarios

Algo muy interesante de la pipeline anterior es que permite hacer
una validación de las `pruebas unitarias` que un desarrollado ha subido
al repositorio remoto.

Al ejecutar la pipeline, se ejecuta `maven` y se puede observar en tiempo real
la validación de las pruebas.

De cada `build` o ejecución, se puede:

- Ver el `status`
- Analizar los `changes`. Ver la `diff` del commit.
- Analizar el `console output`. Ver fallos de las ejecuciones.

Por último, puede que a veces sea necesario eliminar el directorio
de trabajo (del proyecto de estilo libre) en Jenkins con la opción:

> `Wipe Out Current Workspace`

## Integrar Slack en jenkins

Slack es una herramienta de comunicación. Para iniciar sesión se utiliza
el correo y un código recibido a través de este.

Entramos en la páginas de Slack, configuramos un workspace (`roberto`) y creamos
un canal llamado `jenkins`. A continuación agregamos la app llamada
`Jenkins CI` al canal que acabamos de crear.

En un momento dado, desde el propio slack se indica como se debe hacer la 
configuración de jenkins.

Configuración en jenkins:

- Instalar el plugin `slack comunication`
- Navegar a `Configure System - Slack`
  - Slack Workspace: `<Team Subdomain>`
  - Credential de tipo secret text: `<Integration Token Credential ID>`

Las notificaciones de Slack hay que configurarlas para cada proyecto en
el apartado de **Post-Build Actions**.

Probamos la pipeline creada de la misma forma que lo hicimos con la
pipeline anterior. De hecho, esta pipeline es una modificación de la
anterior.

## Activar en Jenkins los reportes de JUnit

En el proyecto de Jenkins, tambíen se puede confiugurar la creación de resúmenes
de nuestros test unitarios de JUnit. Se configuran en el apartado de **Post-Build Actions**.
Hay que añadir un `Publish JUnit test result report` y escribir en `Test report XMLs` la
ruta del workspace donde están los xml, en este caso es `billing/target/surefire-reports/*.xml`

# Entrega continua y despliegue continuo (CI/CD)

Sonarqube, Jenkins y kubernetes.

SonarQube es una plataforma para evaluar código fuente.

## Sonarqube integrado con jenkins

### Instalación con docker

Vamos a instalar Sonarqube utilizando docker. Consultar previamente
la documentación oficial:

```bash
docker pull sonarqube
docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube
```

Hay que crear un red virtual para conectar los contenedores de 
jenkins y sonarqube:

```bash
docker network create jenkins_sonarqube
docker network ls
docker network connect jenkins_sonarqube <jenkins container>
docker network connect jenkins_sonarqube <sonarqube container>
```

### Configuración de sonarqube

Entramos en `localhost:9000`. El user/password por defecto es
`admin/admin` y al entrar se cambiar el password.

Navegamos a `Administration - Security` y genenamos un token que debe
ser guardado en un sitio seguro.

### Configuración de jenkins

Consultar la documentación oficial en
[https://docs.sonarqube.org/.../jenkins/](https://docs.sonarqube.org/latest/analysis/jenkins/)

Instalamos el plugin `SonarQube Scanner`.

Navegamos a `Manage Jenkins - Configure System` para configurar
el servidor:

- Habilitar variables de entorno.
- Name: `sonarqube`
- Server Url: `http://<sonarqube container name>:9000`
- Server authentication token: de tipo secret test `<token generado en el paso anterior>`

**Imporante**. Es necesario utilizar `http://<sonarqube container name>:9000` en lugar de
`http://localhost:9000` para que la pipeline se ejecute correctamente (para que se 
ejecute Sonarqube) a pesar de que al poner esa url queda inutilizado el hipervínculo
del icono de Sonarqube en Jenkins.

Navegamos a `Manage Jenkins - Global Tool Configuration` para configurar
`SonarQube Scanner`. Le ponemos un nombre y añadimos la forma de instalación
y la versión.

## Añadir un escaneo de sonarqube al pipeline de jenkins

A la pipeline anterior, le añadimos un **Build** adicional
llamado `Execute SonarQube Scanner`:

- Task to run: `scan`
- Analysis properties (no escribir comentarios)

```bash
sonar.projectKey=sonarqube # nombre del oproyecto en sonarqube
sonar.sources=billing/src/main/java # ruta de las clases que queremos anzalizar
sonar.java.binaries=billing/target/classes # ruta de los binarios
```

- Additional arguments: `-X` para habilitar el debug

Notas: podemos reordenar el conjunto de los **builds** definidos.
Nos interesa el siguiente orden:

- Evaluar el código con Sonarqube
- Ejecutar el maven para compilar
- Hacer el merge

