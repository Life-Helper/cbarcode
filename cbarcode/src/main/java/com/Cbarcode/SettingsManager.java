package com.Cbarcode;

import java.io.*;
import java.util.Properties;

public class SettingsManager {
    private static final String CONFIG_FILE = "config.properties";

    public static void savePath(String path) {
        Properties properties = new Properties();
        properties.setProperty("filePath", path);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadPath() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            return properties.getProperty("filePath", "");
        } catch (IOException e) {
            // 파일이 없거나 읽기 실패 시 기본값 반환
            String documentsPath = System.getProperty("user.home") + "\\Documents";
            File outputDir = new File(documentsPath, "cbarcode");

            if (!outputDir.exists()) {
              if (!outputDir.mkdirs()) {
                throw new IOException("폴더를 만들지 못했습니다: " + outputDir.getAbsolutePath());
              }
            }

            return documentsPath + "\\cbarcode";
        }
    }
}
