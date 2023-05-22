package com.rfimmortal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class Main {
    public static void main(String[] args) throws Exception{
        String filePath = "./config.json";
        File configFile = new File(filePath);
        String localFolder = "";
        String smbFolder = "";
        String smbHost = "";
        String smbUsername = "";
        String smbPassword = "";
        String smbPort = "";

        if (configFile.exists()) {
            // 文件存在，读取文件内容
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                StringBuilder jsonStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonStr.append(line);
                }

                // 将文件内容转化为JSON对象
                JSONObject jsonObject = JSON.parseObject(jsonStr.toString());
                localFolder = jsonObject.getString("local_folder");
                smbHost = jsonObject.getString("smb_host");
                smbFolder = jsonObject.getString("smb_folder");
                smbUsername = jsonObject.getString("smb_username");
                smbPassword = jsonObject.getString("smb_password");
                smbPort = jsonObject.getString("smb_port");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 文件不存在，创建文件并写入默认内容
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                JSONObject defaultConfig = new JSONObject();
                defaultConfig.put("local_folder", "");
                defaultConfig.put("smb_host", "");
                defaultConfig.put("smb_folder", "");
                defaultConfig.put("smb_username", "");
                defaultConfig.put("smb_password", "");
                defaultConfig.put("smb_port", "445");

                writer.write(defaultConfig.toJSONString());
                writer.flush();

                System.out.println(">>>配置文件已创建并写入默认内容,请完善配置后再启动此程序");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File localFile = new File(localFolder+"/_lastexit_.dsv");
        Path path = localFile.getAbsoluteFile().toPath();
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime fileTime = attrs.lastModifiedTime();
        new DownloadArchive(localFolder,smbFolder,smbHost,smbUsername,smbPassword,smbPort);
        String processName = "DSPGAME.exe";
        String gameId = "1366540";
        if(isProcessRunning(processName) == true){
            System.out.println(">>>游戏正在运行，请关闭游戏后再启动此程序");
            System.exit(0);
        }

        try {
            String steamUrl = "steam://rungameid/" + gameId;
            Desktop.getDesktop().browse(new URI(steamUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(5000);
        long startTime = System.currentTimeMillis();
        System.out.println("\r>>>等待DSPGAME.exe启动...");
        while (isProcessRunning(processName) == false){
            Thread.sleep(3000);
        }
        while (isProcessRunning(processName) == true){
            System.out.print("\r>>>DSPGAME.exe正在运行("+formatTime(System.currentTimeMillis()-startTime)+")");
            Thread.sleep(3000);
        }
        System.out.println();
        Thread.sleep(3000);
        new UploadArchive(localFolder,smbFolder,smbHost,smbUsername,smbPassword,smbPort,fileTime.toMillis());
    }
    public static boolean isProcessRunning(String processName) {
        try {
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").contains("Windows")) {
                processBuilder = new ProcessBuilder("tasklist");
            } else {
                processBuilder = new ProcessBuilder("ps", "-e");
            }
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(processName)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedTime;

    }
}