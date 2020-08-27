package PatternScan;

import PatternScan.Tasks.*;
import Util.ArgParser;
import Util.FileUtil;
import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Entry {

    public static void main(String[] args) {
        ArgParser parser = new ArgParser();
        parser.parse(args);
        String apkDirectory = parser.apkDir;
        String reslutJsonDir = parser.resultJsonPath;
        String sdkName = parser.sdkName;
        int threadsNumber = parser.threadsNumber;
        //创建定长线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadsNumber);
        System.out.println("[+] scan apk file dir ...");
        List<String> apkFileList = getApkListFromJson(apkDirectory);
//        List<String> apkFileList = Util.FileUtil.scanApkRecursion(apkDirectory);
        System.out.println("[+] apk list add finish! scan api ...");

        for (String apk : apkFileList) {
//            FitUseDetect task = new FitUseDetect(apk, reslutJsonDir);
//            Pattern2 task = new Pattern2(apk, reslutJsonDir);
//            Pattern1 task = new Pattern1(apk, reslutJsonDir);
//            AdDetect task = new AdDetect(apk, reslutJsonDir);
//            ScanMeasureTask task = new ScanMeasureTask(apk, reslutJsonDir);
            ScanSameDeidTask task = new ScanSameDeidTask(apk, reslutJsonDir);

            try {
                fixedThreadPool.submit(task);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("scan finish！");
        fixedThreadPool.shutdown();
    }

    public static List<String> getApkListFromJson(String jsonFile) {
        return (List<String>) JSON.parse(FileUtil.readFileStr(jsonFile));
    }

}
