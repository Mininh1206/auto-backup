/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.auto.backup;

import java.time.LocalDateTime;

/**
 *
 * @author Daniel
 */
public class AutoBackup {

    public static void main(String[] args) {
        new BackupDaemon().start();
    }
}
