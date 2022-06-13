# Práctica 004

- Hacer una orquestación con varias imágenes utilizando docker-compose
- Dos entornos distintos: producción y preproducción
- Crear redes
- Limitar recursos cpu, ram

## Comandos

Construir las imagenes definidas en la orquestación:

```bash
docker-compose -f stack-billing.yml build
```

Inicializar los contenedores de los servicios de la orquestación:

```bash
docker-compose -f stack-billing.yml up -d
```

## Navegador

- `http://localhost:7081/` aplicación preproducción
- `http://localhost:8081/` aplicación producción
- `http://localhost:9090` database admin service
