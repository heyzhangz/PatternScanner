package Util;

import org.jf.dexlib2.dexbacked.reference.DexBackedFieldReference;
import org.jf.dexlib2.iface.Method;

public class Report {

    public String callerDefClass;
    public String callerMethod;
    public String dataType;
    public DexBackedFieldReference rawDataType;

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    public Report(Method method, DexBackedFieldReference field, String dataType) {
        this(method.getDefiningClass(), method.getName(), field, dataType);
    }

    public Report(String callerClass, String callerMethod) {
        this.callerDefClass = callerClass;
        this.callerMethod = callerMethod;
        this.dataType = "NONE";
    }

    public Report(Method caller, Method callee, String api) {
        this.callerDefClass = caller.getDefiningClass();
        this.callerMethod = caller.getName();
        this.dataType = api;
    }

    public Report(String callerClass, String callermethod, DexBackedFieldReference field, String type) {
        this.callerDefClass = callerClass;
        this.callerMethod = callermethod;
        this.dataType = type;
        this.rawDataType = field;
    }


}
