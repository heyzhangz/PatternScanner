package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<String> targetApis = null;

    public static void writeFile(String fileName, String data) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String fileName) {
        List<String> fileLines = new ArrayList<>();
        File file = new File(fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLines;
    }

    public static String readFileStr(String fileName) {
        List<String> fl = FileUtil.readFile(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : fl) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public static List<String> scanApkRecursion(String dirPath) {
        List<String> apkFileList = new ArrayList<>();
        File directory = new File(dirPath);
        if(!directory.isDirectory() && !directory.isFile()){
            System.out.println("[+] directory is not exist!");
            System.exit(0);
        }
        if(directory.isDirectory()){
            File [] filelist = directory.listFiles();
            for(int i = 0; i < filelist.length; i ++){
                /**如果当前是文件夹，进入递归扫描文件夹**/
                if(filelist[i].isDirectory()){
                    /**递归扫描下面的文件夹**/
                    scanApkRecursion(filelist[i].getAbsolutePath());
                }
                /**非文件夹**/
                else{
                    String apkPath = filelist[i].getAbsolutePath();
                    if (apkPath.endsWith(".apk")) {
                        apkFileList.add(apkPath);
                    }
                }
            }
        }
        else if (directory.isFile()) {
            if (directory.getAbsolutePath().endsWith(".apk")) {
                apkFileList.add(directory.getAbsolutePath());
            }
        }
        return apkFileList;
    }
}
