/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class Config {
    /* Поля класса, константы */
    public static final String VERSION = "A1.4"; // Версия программы McMapTool
    public static final String DEFAULT_SERVER_NAME = "Dynmap-Сервер"; // Имя сервера добавляемого в список серверов, по-умолчанию

    /* Поля объекта, переменные настроек программы */
    public String server = ""; // Последний запущенный сервер
    public boolean sound = true; // Звук чата
    public float sound_volume = 0.9f; // Громкость
    public boolean record_coords = false; // Запись координат игроков
    public final java.util.List<ServerProfil> serverProfils = Collections.synchronizedList(new ArrayList<ServerProfil>());

    public static class ServerProfil {
        public String name;
        public String url;
    }

    /** Загрузка настроек из файла "config.ini" в поля переменных объекта этого класса */
    public boolean load() {
        Properties p = new Properties();
        File file = new File("config.ini");
        if (!file.exists()) { /* Если нет файла - попытка создать новый */
            p.setProperty("server", server);
            p.setProperty("sound", "" + sound);
            p.setProperty("sound_volume", "" + sound_volume);
            p.setProperty("record_coords", "" + record_coords);
            try {
                FileOutputStream fos = new FileOutputStream("config.ini");
                p.store(fos, null);
                fos.close();
                p.clear();
            } catch (IOException ignored) {}
        } else {
            file = new File("config.ini");
            if (!file.exists()) {
                // Не удалось создать файл "config.ini", в таком случае будут использованы настройки по-умолчанию без файла
                return false;
            }
        }

        /* Если файл есть */
        try {
            FileInputStream fis = new FileInputStream("config.ini");
            p.load(fis);
            server = p.getProperty("server");
            sound = Boolean.parseBoolean(p.getProperty("sound"));
            sound_volume = Float.parseFloat(p.getProperty("sound_volume"));
            record_coords = Boolean.parseBoolean(p.getProperty("record_coords"));
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
