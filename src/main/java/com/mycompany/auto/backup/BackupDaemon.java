/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.auto.backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackupDaemon extends Thread {

    private final JsonHandler jsonHandler;
    private static final String NOMBRE_DISPOSITIVO = "Daniel";
    private static final Logger LOGGER = Logger.getLogger(BackupDaemon.class.getName());

    public BackupDaemon() {
        this.jsonHandler = new JsonHandler();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Listar todas las unidades montadas
                File[] unidades = File.listRoots();
                for (File unidad : unidades) {
                    var etiqueta = obtenerEtiqueta(unidad);

                    LOGGER.log(Level.INFO, "Dispositivo encontrado: {0}", etiqueta);

                    // Detectar dispositivos USB (puedes filtrar por nombre o etiqueta)
                    if (unidad.canRead() && unidad.canWrite() && unidad.getTotalSpace() > 0) {
                        jsonHandler.agregarDispositivo(etiqueta);

                        // Verificar si el dispositivo es "Daniel"
                        if (etiqueta.equalsIgnoreCase(NOMBRE_DISPOSITIVO)) {
                            try {
                                if (unidad.exists()) {
                                    realizarCopiaIncremental(unidad);
                                }
                            } catch (IOException e) {
                                System.err.println("Dispositivo desconectado: " + etiqueta);
                            }
                        }
                    }
                }

                // Esperar 1 minuto antes de volver a escanear
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String obtenerEtiqueta(File unidad) {
        try {
            return Files.getFileStore(unidad.toPath()).name();
        } catch (IOException ex) {
            return "";
        }
    }

    private void realizarCopiaIncremental(File unidad) throws IOException {
        // Leer información de última copia
        List<Dispositivo> dispositivos = jsonHandler.leerDispositivos();
        Dispositivo dispositivo = dispositivos.stream()
                .filter(d -> d.getNombre().equals(obtenerEtiqueta(unidad)))
                .findFirst()
                .orElse(null);

        // Usamos AtomicReference para que pueda ser modificada dentro del visitor
        AtomicReference<LocalDateTime> ultimaCopia = new AtomicReference<>(null);
        try {
            if (dispositivo != null && dispositivo.getUltimaCopia() != null) {
                ultimaCopia.set(LocalDateTime.parse(dispositivo.getUltimaCopia()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fecha inválida para dispositivo: {0}", obtenerEtiqueta(unidad));
        }

        // Directorio de destino
        Path destino = Paths.get("A:/Respaldos/" + obtenerEtiqueta(unidad));
        if (Files.notExists(destino)) {
            Files.createDirectories(destino); // Crear directorio si no existe
        }

        // Usamos AtomicBoolean para la variable copiaExitosa
        AtomicBoolean copiaExitosa = new AtomicBoolean(true); // Inicialmente la copia es exitosa

        // Lista de extensiones a excluir (como .exe y .msi)
        List<String> extensionesExcluidas = Arrays.asList(".exe", ".msi");

        // Recorre los archivos de la unidad
        Path sourcePath = unidad.toPath();
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // Verificar si la extensión del archivo está en la lista de exclusión
                String fileName = file.toString().toLowerCase();
                for (String extension : extensionesExcluidas) {
                    if (fileName.endsWith(extension)) {
                        // Si el archivo tiene una de las extensiones excluidas, no lo copiamos
                        return FileVisitResult.CONTINUE;
                    }
                }

                try {
                    // Verificar si el archivo debe ser copiado (modificado después de la última copia)
                    Path target = destino.resolve(sourcePath.relativize(file));

                    // Asegurarse de que los directorios del destino existan
                    Path targetDir = target.getParent();
                    if (Files.notExists(targetDir)) {
                        Files.createDirectories(targetDir); // Crear directorios si no existen
                    }

                    if (ultimaCopia.get() == null || attrs.lastModifiedTime().toInstant().isAfter(ultimaCopia.get().atZone(ZoneId.systemDefault()).toInstant()) || Files.notExists(target)) {
                        // Copiar archivo
                        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                        
                        LOGGER.log(Level.INFO, "Archivo copiado correctamente: {0}", file);
                    }
                } catch (IOException e) {
                    copiaExitosa.set(false);  // Si ocurre un error, marcamos como falso
                    LOGGER.log(Level.SEVERE, "Error al copiar archivo: {0} - Error: {1}", new Object[]{file, e.getMessage()});
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                copiaExitosa.set(false);  // Si ocurre un fallo en el acceso al archivo, marcamos como falso
                LOGGER.log(Level.SEVERE, "Error al acceder al archivo: {0} - Error: {1}", new Object[]{file, exc.getMessage()});
                return FileVisitResult.CONTINUE;
            }
        });

        // Actualizar la fecha de última copia si todo fue exitoso
        if (copiaExitosa.get() && dispositivo != null) {
            dispositivo.setUltimaCopia(LocalDateTime.now().toString());
            jsonHandler.guardarDispositivos(dispositivos);
        } else {
            LOGGER.log(Level.SEVERE, "Copia incompleta para dispositivo: {0}", obtenerEtiqueta(unidad));
        }
    }
}
