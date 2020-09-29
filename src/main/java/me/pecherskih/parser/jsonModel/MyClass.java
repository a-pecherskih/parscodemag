package me.pecherskih.parser.jsonModel;

import java.util.List;

public class MyClass {
    private String className;
    private String packageName;
    private List<String> methods;

    public MyClass(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        packageName = packageName.replace("\\", "/");
        packageName = packageName.replace(":\\\\", ":/");
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
