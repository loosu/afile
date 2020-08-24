# AFile

## 一·  导入

### 1.1 添加JitPack仓库地址

build.gradle(:Project)

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```



### 2.2 添加jar依赖

build.gradle(:Module)

```groovy
	dependencies {
	        implementation 'com.github.loosu:afile:Tag'
	}
```



## 二.  使用

### 2.1 扫描

获取文件数和文件夹数以及文件总大小。

```java
Scan.Result result = AFile.scan().setListener(actionListener)
        .append(file)
        .start();
```



### 2.2 拷贝

```java
AFile.copy().setListener(actionListener)
        .append(new File("/sdcard/DCIM"))
        .append(new File("/sdcard/DCIM/Camera"))
        .setDst(getFilesDir())
        .start();
```



### 2.3 删除

```java
AFile.delete().setListener(actionListener)
        .setScanListener(actionListener)
        .append(getFilesDir())
        .start();
```



### 2.4 压缩

```java
AFile.zip().setListener(actionListener)
        .append(new File("/sdcard/DCIM/Camera"))
        .setDst(new File(getFilesDir(), "1.zip"))
        .start();
```



### 2.5 解压

```java
AFile.unzip().setListener(actionListener)
        .source(new File(getFilesDir(), "1.zip"))
        .setDst(new File(getFilesDir(), "1unzip"))
        .start();
```