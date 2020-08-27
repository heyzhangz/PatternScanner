package PatternScan.Tasks;

import Util.ApkFile;
import Util.Scan;

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction35c;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.Format;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21c;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScanSameIDTask extends Scan {

    public String[] SP_PATTERN_LIST = {"35",
        "Landroid/os/Build;->BOARD", "Landroid/os/Build;->BRAND",
        "Landroid/os/Build;->CPU_ABI", "Landroid/os/Build;->DEVICE",
        "Landroid/os/Build;->DISPLAY", "Landroid/os/Build;->HOST",
        "Landroid/os/Build;->ID", "Landroid/os/Build;->MANUFACTURER",
        "Landroid/os/Build;->MODEL", "Landroid/os/Build;->PRODUCT",
        "Landroid/os/Build;->TAGS", "Landroid/os/Build;->TYPE",
        "Landroid/os/Build;->USER"};

    public ScanSameIDTask (String apkPath, String resDirPath) {
        super(apkPath, resDirPath);
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

            String apkRes = "";
            for (String DexEntryName : apkDexFiles.getDexEntryNames()) {
                DexFile dexFile = apkDexFiles.getEntry(DexEntryName);
                Set<ClassDef> allClasses = new HashSet<>();
                allClasses.addAll(dexFile.getClasses());

                // 遍历所有类和方法
                for (ClassDef clazz : allClasses) {

                    if (clazz.toString().startsWith("Landroid/")
                            || clazz.toString().startsWith("Landroidx/")
                            || clazz.toString().startsWith("Ljava/")
                            || clazz.toString().startsWith("Ljavax/")
                            || clazz.toString().startsWith("Lcom/google/android/gms/")) {
                        continue;
                    }

                    String className = clazz.toString();

                    for (Method method : clazz.getMethods()) {
                        apkRes += findMethodPattern(method, className);
                    }
                }
            }

            if (!apkRes.equals("")) {
                System.out.println("[+] " + packageName + " {" + apkPath + "}");
                System.out.print(apkRes);
            }

//            DexFile dexFile = DexFileFactory.loadDexFile(apkFile, Opcodes.getDefault());
//            Set<ClassDef> allClasses = new HashSet<ClassDef>();
//            allClasses.addAll(dexFile.getClasses());
//
//            // 遍历所有类和方法
//            for (ClassDef clazz : allClasses) {
//                if (clazz.toString().startsWith("Landroid/")
//                        || clazz.toString().startsWith("Landroidx/")
//                        || clazz.toString().startsWith("Ljava/")
//                        || clazz.toString().startsWith("Ljavax/")
//                        || clazz.toString().startsWith("Lcom/google/android/gms/")) {
//                    continue;
//                }
//
//                for (Method method : clazz.getMethods()) {
//
//
//                    findMethodPattern(clazz, method);
//                }
//            }

        } catch(IOException e){
            e.printStackTrace();
        }

        return apiScanRes;
    }

    public String findMethodPattern(Method method, String className) {

        MethodImplementation mImpl = method.getImplementation();

        int spPatternCount = 0;
        boolean openUDIDCall = false;
        boolean aliUDIDCall = false;
        boolean aliUDIDDirCall = false;
        boolean umengUDIDCall = false;

        try {
            for (Instruction ins : mImpl.getInstructions()) {

                // 扫描字符串
                if (ins.getOpcode().format == Format.Format21c) {

                    DexBackedInstruction21c ins21c = (DexBackedInstruction21c)ins;
                    String ref = ins21c.getReference().toString();

                    if (spPatternCount < 14 && ref.startsWith(this.SP_PATTERN_LIST[spPatternCount])) {
                        spPatternCount++;
                    }

                    if (!openUDIDCall && (ref.contains("OpenUDID_manager") || ref.contains("getOpenUDID"))) {
                        openUDIDCall = true;
                    }

                    if (!method.getName().contains("readUtdid") && !method.getName().contains("getValue")
                            && !method.getName().contains("saveUtdidToNewSettings")) {
                        if (!aliUDIDCall && ref.startsWith("mqBRboGZkQPcAkyk")) {
                            aliUDIDCall = true;
                        }
                    }

                    if (!umengUDIDCall && (ref.contains("Alvin2") || ref.contains(".um/sysid.dat"))) {
                        umengUDIDCall = true;
                    }

                }

                // 扫描函数调用
                if (ins.getOpcode().format == Format.Format35c) {

                    DexBackedInstruction35c ins35c = (DexBackedInstruction35c)ins;
                    String ref = ins35c.getReference().toString();

                    if (!openUDIDCall && (ref.contains("OpenUDID_manager") || ref.contains("getOpenUDID"))) {
                        openUDIDCall = true;
                    }

                    if (!aliUDIDDirCall && ref.contains("Lcom/ta/utdid2/device/UTUtdid;") &&
                            ref.contains("getValue") && !className.startsWith("Lcom/ta/utdid2/device/")) {
                        aliUDIDDirCall = true;
                    }
                }

            }
        } catch (NullPointerException e) {
            return "";
        }


        if (spPatternCount == 14)
            return getResultStr("[sp]", className, method.getName());

        if (openUDIDCall)
            return getResultStr("[openUDID]", className, method.getName());

        if (umengUDIDCall)
            return getResultStr("[umengUDID]", className, method.getName());

        if (aliUDIDCall)
            return getResultStr("[aliPattern]", className, method.getName());

        if (aliUDIDDirCall)
            return getResultStr("[aliUDID]", className, method.getName());

        return "";
    }

    private String getResultStr(String tag, String clsname, String methodname) {

        StringBuilder resstr = new StringBuilder("    ");
        resstr.append(tag);
        resstr.append(clsname);
        resstr.append("->");
        resstr.append(methodname);
        resstr.append("\n");

        return resstr.toString();
    }

    private String getResultStr(String tag, String clsname, String methodname, String comm) {

        StringBuilder resstr = new StringBuilder("    ");
        resstr.append(tag);
        resstr.append(clsname);
        resstr.append("->");
        resstr.append(methodname);
        resstr.append("\t");
        resstr.append(comm);
        resstr.append("\n");

        return resstr.toString();
    }

    public static void main(String[] args) {

        String apk = "D:\\Download\\ans\\apks\\com.boo.boomoji.apk";
//        String apk = "D:\\Download\\countly.dex";
        String out = ".\\";

        ScanSameIDTask task = new ScanSameIDTask(apk, out);
        task.scan(apk);

    }
}
