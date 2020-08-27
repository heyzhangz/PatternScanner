package Util;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.MultiDexContainer;

import java.io.File;
import java.io.IOException;


public class ApkFile {
    private MultiDexContainer<? extends DexBackedDexFile> dexContainer;

    public MultiDexContainer<? extends DexBackedDexFile> getDexContainer(){
        return dexContainer;
    }


    public static ApkFile loadFromFile(File apkFile){
        ApkFile apk = new ApkFile();
        try {
            apk.dexContainer = DexFileFactory.loadDexContainer(apkFile, Opcodes.getDefault());
            return apk;
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (DexFileFactory.UnsupportedFileTypeException e) {
            e.printStackTrace();
        }
        return null;
    }
}

