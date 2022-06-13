# indica la imagen base que vamos a tomar
FROM nginx:alpine 
#Install java 8
# ejecuta dos comandos
RUN apk -U add openjdk8 \ 
    && rm -rf /var/cache/apk/*;
# ejecuta un comando
RUN apk add ttf-dejavu 

#Install java microservice
ENV JAVA_OPTS=""
# indica la ruta donde está el archivo .jar (microservicio)
ARG JAR_FILE 
# asigna un nombre al archivo jar
ADD ${JAR_FILE} app.jar 

#Install app on nginx serve
 # use a volume is mor efficient and speed that filesystem
VOLUME /tmp
# ejecuta un comando
RUN rm -rf /usr/share/nginx/html/* 
# copia fichero de configuración
COPY nginx.conf /etc/nginx/nginx.conf 
# copia la aplicación en angular
COPY dist/billingApp /usr/share/nginx/html 
# copia script
COPY appshell.sh appshell.sh 

#expose ports 8080 for java swagger app and 80 for nginx app
# expone el puerto 80 y el puerto 8080
EXPOSE 80 8080
# se ejecuta en el momento de levantar/inicializar el contenedor
ENTRYPOINT ["sh", "/appshell.sh"] 