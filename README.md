# Auto Backup

Esta aplicación realiza copias de seguridad incrementales de un dispositivo USB específico cada vez que se conecta a la computadora. Utiliza Java y la librería Gson para manejar la información de los dispositivos y sus copias de seguridad. El proceso de copia es automático y se realiza en segundo plano.

## Requisitos

- Java 21 o superior
- La librería Gson (se incluye en el proyecto)
- Un dispositivo USB (llamado "Daniel" por defecto)

## Instrucciones

### 1. Cambiar el nombre del dispositivo USB

Por defecto, la aplicación está configurada para hacer la copia de seguridad del dispositivo USB llamado "Daniel". Si deseas realizar copias de seguridad de otro dispositivo, debes cambiar el valor de la constante `NOMBRE_DISPOSITIVO` en el archivo `BackupDaemon.java` (línea 21):

```java
private static final String NOMBRE_DISPOSITIVO = "Daniel";
```
Sustituye `"Daniel"` por el nombre de tu dispositivo USB.

### 2. Cambiar la ruta de destino de las copias de seguridad

La ruta por defecto donde se almacenan las copias de seguridad es `A:/Respaldos/`. Si deseas cambiar la ruta de destino, puedes hacerlo en la línea 98 del archivo `BackupDaemon.java`:

```java
Path destino = Paths.get("A:/Respaldos/" + obtenerEtiqueta(unidad));
```

Sustituye `"A:/Respaldos/"` por la ruta donde desees almacenar los archivos.

### 3. Cambiar las extensiones de archivo excluidas

La aplicación excluye archivos con extensiones `.exe` y `.msi` de la copia de seguridad por defecto. Si deseas excluir otras extensiones, puedes modificarlas en la línea 107 del archivo `BackupDaemon.java`:

```java
List<String> extensionesExcluidas = Arrays.asList(".exe", ".msi");
```

Añade o elimina extensiones según lo necesites.

## Ejecución

1. Clona el repositorio en tu máquina local.
2. Asegúrate de que tienes Java 21 o superior instalado.
3. Ejecuta la clase `BackupDaemon` para iniciar el proceso de copia de seguridad en segundo plano.

## Licencia

Este proyecto está bajo la licencia [MIT](LICENSE).
