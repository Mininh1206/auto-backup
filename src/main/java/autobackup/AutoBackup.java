/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package autobackup;

/**
 *
 * @author Daniel
 */
public class AutoBackup {
    public final static String RUTA_PROYECTO = System.getProperty("user.home") + "/Documents/Auto Backup/";

    public static void main(String[] args) {
        new BackupDaemon().start();
    }
}
