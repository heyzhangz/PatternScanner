package Util;

import org.jf.dexlib2.iface.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

public class Scan implements Callable {

    public static String[] WHITE_CLASS_LIST = {
            "Landroid/",
            "Landroidx/",
            "Ljava/",
            "Ljavax/"
    };

    public String apkFilePath;
    public String reslutJsonDir;

    public Scan(String apkPath, String reslutPath) {
        apkFilePath = apkPath;
        reslutJsonDir = reslutPath;
    }

    public Scan() {
    }

    public static boolean isSkipPackage(String clzName, String[] extendList) {

        if(isSkipPackage(clzName))
            return true;

        for(String e : extendList)
            if(clzName.startsWith(e))
                return true;

        return false;
    }

    public static boolean isSkipPackage(String clzName) {

        for(String e : WHITE_CLASS_LIST)
            if(clzName.startsWith(e))
                return true;

        return false;
    }

    public static void main(String[] args) {
        String demoApk = "/Users/blackmax/Desktop/GMS/pay/ticketsmaster.apk";
        Map<String, String> apk = new HashMap<>();
        apk.put("ver", "1.0");
        apk.put("path", demoApk);
//        PrivacyTreeScan s = new PrivacyTreeScan(apk, "a");
//        Pair ret = s.scan(demoApk, s.api2Node);
//        Map<String, NodeScan> apkScanRes = (Map<String, NodeScan>) ret.getValue1();
//        String resultJson = JSON.toJSONString(apkScanRes, SerializerFeature.PrettyFormat);
    }

    public Object scan(String apkPath) {
        return false;
    }


    private Object scanClass(ClassDef clazz) {
        return null;
    }


    @Override
    public Object call() throws Exception {
//        System.out.println("Thread-" + Thread.currentThread().getId() + "start!");
//        System.out.println(String.format("[+] scan %s ing ...", apkFilePath));
        scan(apkFilePath);
//        List<Report> scanRes = (List<Report>) scan(apkFilePath);
//        if (scanRes.size() > 0) {
//            System.out.println(this.apkFilePath);
//            Map<String, List<Map<String, String>>> jsonRes = convert2Json(scanRes);
//            String resultJson = JSON.toJSONString(jsonRes, SerializerFeature.PrettyFormat);
//            String resultPath = genJsonPath(apkFilePath, reslutJsonDir);
//            FileUtil.writeFile(resultPath, resultJson);
//        }
        return null;
    }

    private Map<String, List<Map<String, String>>> convert2Json(List<Report> scanRes) {
        Map<String, List<Map<String, String>>> jsonRes = new HashMap<>();
        for (Report r : scanRes) {
            String tag = r.dataType;
            if (!jsonRes.keySet().contains(tag)) {
                List<Map<String, String>> tmp = new ArrayList<>();
                jsonRes.put(tag, tmp);
            }
            Map<String, String> tmp = new HashMap<>();
            tmp.put("CallerClass", r.callerDefClass);
            tmp.put("CallerMethod", r.callerMethod);
            jsonRes.get(tag).add(tmp);
        }
        return jsonRes;
    }


    private static String genJsonPath(String apk, String reslutJsonDir) {
        String[] p = apk.split(File.separator);
        String f = p[p.length - 1].replace(".apk", ".json");
        return reslutJsonDir + File.separator + f;
    }
}