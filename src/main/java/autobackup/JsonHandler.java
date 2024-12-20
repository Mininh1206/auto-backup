/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package autobackup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    private static final String RUTA_JSON = AutoBackup.RUTA_PROYECTO + "dispositivos.json";
    private final Gson gson;

    public JsonHandler() {
        this.gson = new Gson();
    }

    public List<Dispositivo> leerDispositivos() {
        try (Reader reader = new FileReader(RUTA_JSON)) {
            Type tipoLista = new TypeToken<List<Dispositivo>>() {
            }.getType();

            List<Dispositivo> dispositivos = gson.fromJson(reader, tipoLista);

            // Reparar datos faltantes
            for (Dispositivo d : dispositivos) {
                if (d.getUltimaCopia() == null) {
                    d.setUltimaCopia(null); // Asegurar compatibilidad
                }
            }
            return dispositivos;
        } catch (IOException e) {
            return new ArrayList<>(); // Devuelve lista vacía si no existe el archivo
        }
    }

    public void guardarDispositivos(List<Dispositivo> dispositivos) {
        try {
            dispositivos.forEach(d -> {
                if (d.getUltimaCopia() == null) {
                    d.setUltimaCopia("Sin copia"); // Inicializa con un valor por defecto
                }
            });

            // Si no existe las carpetas, las creamos
            if (!Paths.get(RUTA_JSON).getParent().toFile().exists()) {
                if (!Paths.get(RUTA_JSON).getParent().toFile().mkdirs()) {

                    throw new Exception("Error al crear las carpetas");

                }
            }

            try (Writer writer = new FileWriter(RUTA_JSON)) {
                gson.toJson(dispositivos, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agregarDispositivo(String nombre) {
        List<Dispositivo> dispositivos = leerDispositivos();

        if (dispositivos.stream().noneMatch(d -> d.getNombre().equals(nombre))) {
            dispositivos.add(new Dispositivo(nombre, null)); // Incluye `ultimaCopia: null`
            guardarDispositivos(dispositivos);
        }
    }

}
