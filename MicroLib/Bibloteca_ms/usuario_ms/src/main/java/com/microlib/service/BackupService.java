package com.microlib.service;

import org.springframework.stereotype.Service;

@Service
public class BackupService {

    public void createBackup() {
        String dumpPath = "C:/laragon/bin/mysql/mysql-8.4.3-winx64/bin/mysqldump.exe";
        String dbName = "db_usuario";
        String dbUser = "root";
        String dbPass = "";
        String savePath = "C:/backups/backup_db_usuario.sql";

        String command = String.format(
                "\"%s\" -u %s %s --databases %s -r \"%s\"",
                dumpPath,
                dbUser,
                dbPass.isEmpty() ? "" : "-p" + dbPass,
                dbName,
                savePath
        );

        try {
            Process process = Runtime.getRuntime().exec(command);
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup creado con éxito en: " + savePath);
            } else {
                System.err.println("Fallo al crear el backup");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}