Para ejecutar el servidor en modo desarrollo primero añade las siguientes variables de entorno a nivel de sistema operativo o dentro de tu IDE preferido (Eclipse, Visual Studio, IntelliJ...):

Backend
1. Importar el backend en Eclipse u otro IDE: importar la carpeta zahori-server-src/backend
2. Definir las siguientes variables de entorno
En Eclipse: Sobre el fichero ZahoriServerApp.java -> Run as... configurations -> pestaña Environments

ZAHORI_DB_HOST=localhost

ZAHORI_DB_NAME=zahori

ZAHORI_DB_PASS=Z@h0r12O2O

ZAHORI_DB_PORT=5432

ZAHORI_DB_SCHEMA=public

ZAHORI_DB_USER=zahori

ZAHORI_EUREKA_HOST=localhost

ZAHORI_EUREKA_PORT=8761

ZAHORI_SELENOID_UI_EXTERNAL_HOST=localhost

ZAHORI_SELENOID_UI_EXTERNAL_PORT=8080

ZAHORI_SELENOID_UI_INTERNAL_HOST=localhost

ZAHORI_SELENOID_UI_INTERNAL_PORT=8081

ZAHORI_SERVER_PORT=9090

3. Iniciar el backend:
Eclipse: ZahoriServerApp.java -> Run as... java application
→ Cuando arranque el backend estará escuchando en: localhost:9090
