package com.rfimmortal;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Properties;

public class UploadArchive {
    public UploadArchive(String localFileFolder,String smbFolder,String smbHost,String username,String password,String smbPort,long time) throws Exception{
        Properties ps = new Properties();
        ps.setProperty("jcifs.smb.client.domain", smbHost);
        ps.setProperty("jcifs.smb.client.username", username);
        ps.setProperty("jcifs.smb.client.password", password);
        ps.setProperty("jcifs.smb.client.dfs.disabled", "true");
        CIFSContext cifs = new BaseContext(new PropertyConfiguration(ps));
        SmbFile smbFile = new SmbFile("smb://"+smbHost+":"+smbPort+"/"+smbFolder+"/_lastexit_.dsv",cifs);
        File localFile = new File(localFileFolder+"/_lastexit_.dsv");
        Path path = localFile.getAbsoluteFile().toPath();
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime fileTime = attrs.lastModifiedTime();
        if(smbFile.getLastModified() == time){
            System.out.println(">>>本地存档未修改，无需上传");
        }else {
            InputStream inputStream = new FileInputStream(localFile);
            OutputStream outputStream = new SmbFileOutputStream(smbFile);
            byte[] buffer = new byte[65536];
            int len = 0;
            long transferred = 0;
            long total = inputStream.available();
            System.out.println(">>>开始上传存档...");
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                transferred = transferred + len;
                new ProgressBar(transferred, total);
                outputStream.write(buffer, 0, len);
            }
            System.out.println("");
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            smbFile.setLastModified(fileTime.toMillis());
            System.out.println(">>>上传完成");
        }
    }
}
