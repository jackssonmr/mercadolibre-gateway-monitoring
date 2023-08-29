# MercadoLibre Api Gateway (SpringCloud, Prometheus, Grafana y redis)
En esta prueba de concepto utilice [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/reference/html/) con [Prometheus](https://prometheus.io/) y [Grafana](https://grafana.com/).  

Para simplificar el proceso de ejecucion utilice Docker-compose

## Acerca del Gateway
Mezclando todas estas tecnologias pienso que se mantiene todo se implementa de manera simple.
- Spring Cloud Gateway recopila métricas y las expone en `/actuator/prometheus`
- Prometheus recopila métricas y las guarda en su base de datos
- Grafana obtiene sus métricas de Prometheus y las muestra en gráficos elegantes. O envía alertas. O hace otras cosas.
- Redis genero cache temporal para las solicitudes con lo que puedo establecer limites de llamados en un rango de tiempo preestablecido.


### Métricas personalizadas
Todas las ubique en la clase [PrometheusGlobalFilter](/filter/PrometheusGlobalFilter.java).
Son muy senciallas para este escenario, estoy contando cantidad de solicitudes y respuestas gestionadas.

## Ejecutar el codigo
Siga los pasos acontinuacion para poner en marcha el escenario

### Prerequisitos
Para ejecutar la aplicacion se necesita:
- docker
- docker-compose
- JDK 17 o posterior
- maven

### Ejecute la aplicacion
Compoilar el projecto  
```shell
mvn clean package
```

Ejecute docker-compose 
```shell
docker-compose up
```

Si ejecuta `docker ps` observara que tiene los contenedores en ejecucion
```shell
CONTAINER ID   IMAGE                         COMMAND                  CREATED        STATUS        PORTS                     NAMES
a1b67fc540b3   grafana/grafana:latest        "/run.sh"                10 hours ago   Up 10 hours   0.0.0.0:3000->3000/tcp    mercadolibre-gateway-monitoring-grafana-1
b1fa04a18bca   prom/prometheus:latest        "/bin/prometheus --w…"   10 hours ago   Up 10 hours   0.0.0.0:9090->9090/tcp    mercadolibre-gateway-monitoring-prometheus-1
d170d82de566   spring-cloud-gateway:latest   "java -jar /app.jar"     10 hours ago   Up 10 hours   0.0.0.0:8080->8080/tcp    mercadolibre-gateway-monitoring-mercadolibre-gateway-1
4b85870d9c70   redis:latest                  "docker-entrypoint.s…"   6 months ago   Up 12 hours   0.0.0.0:32768->6379/tcp   redis-JULf

```
En la siguiente ruta http://localhost:9090/targets?search= se deberia ver que Prometheus donde puede conectarse a tu puerta de enlace y recibir sus métricas.

### Tablero de control
Para ver todos los datos en Grafana, primero debe iniciar sesión y crear algunos paneles.

Navegar a http://localhost:3000 e inicie sesión con el usuario `admin` y la contraseña `admin`. En la barra lateral puedes importar paneles.

**Hay que asegurarse de establecer 'Prometheus' como origen de datos para los tableros.**

Utilice el tablero [12900](https://grafana.com/grafana/dashboards/12900-springboot-apm-dashboard/) credao por prakarsh.
