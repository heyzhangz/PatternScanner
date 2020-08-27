package PatternScan.Tasks;

import Util.ApkFile;
import Util.Scan;

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScanMeasureTask extends Scan {
    public ScanMeasureTask (String apkPath, String resDirPath) {
        super(apkPath, resDirPath);
    }

    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return list.isEmpty()?"":sb.toString().substring(0, sb.toString().length() - 1);
    }

    public Map<String, List<String>> scan(String apkPath) {
        String packageName = apkPath.split("/")[7];
        String[] packageParts = packageName.split("\\.");
        String prefix = "";
        int n = (packageParts.length > 3) ? 3 : packageParts.length;
        String[] parts = Arrays.copyOfRange(packageParts, 0, n);
        prefix = String.join(".", parts);
        Map<String, List<String>> apiScanRes = new HashMap<>();
        File apkFile = new File(this.apkFilePath);
        try {
            ApkFile apk = ApkFile.loadFromFile(apkFile);
            MultiDexContainer<? extends DexBackedDexFile> apkDexFiles = apk.getDexContainer();
            for (String DexEntryName : apkDexFiles.getDexEntryNames()) {
                DexFile dexFile = apkDexFiles.getEntry(DexEntryName);
                Set<ClassDef> allClasses = new HashSet<ClassDef>();
                allClasses.addAll(dexFile.getClasses());
                // 遍历所有类和方法
                for (ClassDef clazz : allClasses) {
                    if (clazz.toString().startsWith("Landroid/")
                            || clazz.toString().startsWith("Landroidx/")
                            || clazz.toString().startsWith("Ljava/")
                            || clazz.toString().startsWith("Ljavax/")
                            || clazz.toString().startsWith("Lcom/google/android/gms/fitness/")) {
                        continue;
                    }
                    // 扫描包名里是不是有包名开头，且含有analysis、measure、tracker等关键词
                    String className = clazz.toString().replace("/", ".");
                    if (
                            ((className.toLowerCase().contains("measur")) ||
                            (className.toLowerCase().contains("track")) ||
                            (className.toLowerCase().contains("analys"))) &&
                            (className.startsWith("L" + prefix))) {
                        if (!apiScanRes.keySet().contains(packageName)) {
                            System.out.println("[+] "+ apkPath);
                            apiScanRes.put(packageName, new ArrayList<>());
                        }
                        System.out.println("    "+ className);
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        return apiScanRes;
    }
}
