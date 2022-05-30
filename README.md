# MinIODemo
> 一个简单的springboot+minio文件服务器搭建的文件上传示例

## 注意事项
> 1. minio下载地址：https://min.io/download#/kubernetes 选择自己适合的环境版本进行下载，
     启动命令为：`.\minio.exe server D:/software/minioData --console-address ":9000" --address ":9001"`，
     `D:/software/minioData`为minio上传文件存放位置（通过在minio.exe文件存放路径下进入cmd执行 `minio.exe server D:\minio\minioData` 命令创建）
> 2. springboot版本：2.6.0
> 3. minio版本：8.2.2
