package PatternScan.Tasks;

import Util.ApkFile;
import Util.Scan;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.instruction.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.util.ExceptionWithContext;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanImportTask extends Scan {

    public static String[] ALI_SDK_PACKAGENAME = {
            "Lcom/umeng",
            "Lcom/ta/utdid",
            "Lcom/amap",
            "Lcom/loc",
            "Lcom/networkbench"
    };

    public ScanImportTask (String apkPath, String resDirPath) {
        super(apkPath, resDirPath);
    }

    public String isAliSDK(String clzName) {

        for(String e : ALI_SDK_PACKAGENAME)
            if(clzName.startsWith(e))
                return e;

        return "";
    }

    public Map<String, List<String>> scan(String apkPath) {

        String packageName = apkPath.split("/")[7];
//        String packageName = apkPath.split("\\\\")[4];
//        String packageName = "com.hld.anzenbokusufakelite";

        Map<String, List<String>> apiScanRes = new HashMap<>();
        File apkFile = new File(this.apkFilePath);

        try {
            ApkFile apk = ApkFile.loadFromFile(apkFile);
            MultiDexContainer<? extends DexBackedDexFile> apkDexFiles = apk.getDexContainer();
            Map<String, Set<String>> importmap = new HashMap<>(); // 该class引入包的全集

            String apkRes = "";
            for (String DexEntryName : apkDexFiles.getDexEntryNames()) {
                DexFile dexFile = apkDexFiles.getEntry(DexEntryName);
                Set<ClassDef> allClasses = new HashSet<>();
                allClasses.addAll(dexFile.getClasses());

                // 遍历所有类和方法, 获取全部完整包名
                for (ClassDef clazz : allClasses) {

                    String clazzName = clazz.toString();

                    if (isSkipPackage(clazzName))
                        continue;

                    // 扫描父类
                    String superName = clazz.getSuperclass();
                    String t = isAliSDK(superName);
                    if (!t.equals("") && !clazzName.startsWith(t))
                        putNew(clazzName, t, importmap);

                    // 扫描接口
                    List<String> interfaceList = clazz.getInterfaces();
                    for(String infa : interfaceList) {
                        t = isAliSDK(infa);
                        if (!t.equals("") && !clazzName.startsWith(t))
                            putNew(clazzName, t, importmap);
                    }

                    // 扫描成员变量
                    for(Field field : clazz.getFields()) {
                        String fieldType = field.getType();
                        t = isAliSDK(fieldType);
                        if (!t.equals("") && !clazzName.startsWith(t))
                            putNew(clazzName, t, importmap);
                    }

                    // 扫描方法
                    for (Method method : clazz.getMethods()) {
                        // 返回值
                        String returnType = method.getReturnType();
                        t = isAliSDK(returnType);
                        if (!t.equals("") && !clazzName.startsWith(t))
                            putNew(clazzName, t, importmap);

                        // 参数列表
                        for(MethodParameter mp : method.getParameters()) {
                            String mpType = mp.getType();
                            t = isAliSDK(mpType);
                            if (!t.equals("") && !clazzName.startsWith(t))
                                putNew(clazzName, t, importmap);
                        }

                        // 指令
                        try {
                            for(Instruction ins : method.getImplementation().getInstructions()) {
                                Set<String> ts = readTypeFromInstruction(ins);
                                for(String e : ts) {
                                    t = isAliSDK(e);
                                    if (!t.equals("") && !clazzName.startsWith(t))
                                        putNew(clazzName, t, importmap);
                                }
                            }
                        } catch(NullPointerException e) {
                        }
                    }
                }
            }

            if (!importmap.isEmpty()) {
                System.out.println("[+] " + packageName + " {" + apkPath + "}");
                for(String e : importmap.keySet()) {
                    System.out.println("\t[-] " + e);
                    for (String ee : importmap.get(e))
                        System.out.println("\t\t[*] " + ee);
                }
            }

        } catch(IOException e){
            e.printStackTrace();
        }

        return apiScanRes;
    }

    private Set<String> readTypeFromInstruction(Instruction ins) {

            if(!(ins instanceof ReferenceInstruction)) {
                return null;
            }

            ReferenceInstruction rins = (ReferenceInstruction)ins;
            String insref = rins.getReference().toString();

            return getClassFromReference(insref);
    }

    private Set<String> getClassFromReference(String ref) {

        Set<String> tempTypes = new HashSet<>();

        Pattern r = Pattern.compile("(?<class>L.*?;)");
        Matcher m = r.matcher(ref);

        while(m.find())
            tempTypes.add(m.group("class"));

        return tempTypes;
    }

    private Map<String, Set<String>> putNew(String clazz, String sdk, Map<String, Set<String>> im) {

        Pattern p = Pattern.compile("L.*?/.*?/.*?(?=/)");
        Matcher m = p.matcher(clazz);

        String clazzpkg;
        if(m.find())
            clazzpkg = m.group();
        else
            clazzpkg = clazz.substring(0, clazz.lastIndexOf("/"));

        if(clazzpkg.startsWith("Lcom/autonavi")) {
            clazzpkg = "Lcom/autonavi";
        } else {
            for(String alisdk : ALI_SDK_PACKAGENAME) {
                if(clazzpkg.startsWith(alisdk)) {
                    clazzpkg = alisdk;
                    break;
                }
            }
        }

        if(!clazzpkg.trim().isEmpty() && !im.containsKey(clazzpkg))
            im.put(clazzpkg, new HashSet<>());

        im.get(clazzpkg).add(sdk);

        return im;
    }

    public static void main(String[] args) {

        String apk = "D:\\Download\\ans\\apks\\ali_APP\\com.taobao.trip.apk";
//        String apk = "D:\\Download\\countly.dex";
        String out = ".\\";

        ScanImportTask task = new ScanImportTask(apk, out);
        task.scan(apk);

    }
}
