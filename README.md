# DysonCloudArchive 戴森球计划云存档
DysonCloudArchive 戴森球计划云存档 **不修改客户端**  
**首先需要一个SMB服务器**  
下载DysonCloudArchive-1.0-SNAPSHOT-jar-with-dependencies.jar  
在同一级目录下创建文件config.json(不手动创建会自动生成)然后填写具体配置  
**注意用"/"而不是反斜杠**  
smb_host可以填域名
```
{
"local_folder":"C:/Users/Users/Documents/Dyson Sphere Program/Save",
"smb_host":"192.168.1.2",
"smb_folder":"SMBShareName/DysonCloudArchive",
"smb_username":"username",
"smb_password":"password",
"smb_port":"445"
}
```
**推荐使用java20运行,其他版本没试过**  
没有配置环境变量需要将java替换为完整的路径。
```
java -jar DysonCloudArchive-1.0-SNAPSHOT-jar-with-dependencies.jar  
```
可以在DysonCloudArchive-1.0-SNAPSHOT-jar-with-dependencies.jar同一级目录下创建一个bat文件以快速启动。


**已知的问题：**  
1.上传和下载的速度只有100Mbps左右
