package com.rfimmortal;


import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.SmbFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Properties;

public class DownloadArchive {
    public DownloadArchive(String localFileFolder,String smbFolder,String smbHost,String username,String password,String smbPort) throws Exception{
        Properties ps = new Properties();
        ps.setProperty("jcifs.smb.client.domain", smbHost);
        ps.setProperty("jcifs.smb.client.username", username);
        ps.setProperty("jcifs.smb.client.password", password);
        ps.setProperty("jcifs.smb.client.dfs.disabled", "true");
        CIFSContext cifs = new BaseContext(new PropertyConfiguration(ps));
        SmbFile smbFile = new SmbFile("smb://"+smbHost+":"+smbPort+"/"+smbFolder+"/_lastexit_.dsv",cifs);
        File localFile = new File(localFileFolder+"/_lastexit_.dsv");
        boolean smbFileExists = smbFile.exists();
        boolean localFileExists = localFile.exists();
        if(smbFileExists == true && localFileExists == true){
            Path path = localFile.getAbsoluteFile().toPath();
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime fileTime = attrs.lastModifiedTime();
            long localFileTime = fileTime.toMillis();
            long smbFileTime = smbFile.getLastModified();
            if(localFileTime >= smbFileTime){
                System.out.println(">>>本地存档为最新");
            }else{
                System.out.println(">>>服务器存档为最新,开始下载...");
                download(smbFile,localFile);
            }
        } else if (smbFileExists == false && localFileExists == true) {
            System.out.println(">>>服务器无存档");
        } else if (smbFileExists == true && localFileExists == false) {
            System.out.println(">>>本地无存档，开始下载...");
            download(smbFile,localFile);
        }else{
            System.out.println("服务器和本地都没有存档，请新建存档或检查配置文件");
        }
    }
    private static void download(SmbFile smbFile,File localFile) throws Exception{
        OutputStream outputStream = smbFile.getOutputStream();
        InputStream inputStream = new FileInputStream(localFile);
        byte[] buffer = new byte[65536];
        int len = 0;
        long transferred = 0;
        long total = inputStream.available();
        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            transferred = transferred+len;
            new ProgressBar(transferred,total);
            outputStream.write(buffer, 0, len);
        }
        System.out.println("");
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        System.out.println(">>>下载完成");
    }
}
