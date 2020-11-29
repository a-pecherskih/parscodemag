package me.pecherskih.parser.model;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileInfo {
    private File me;
    private FileInfo parentFile;

    private String name;
    private String type;

    private List<FileInfo> children;
    private List<String> methods;

    public FileInfo(File me) {
        this.me = me;
        this.parentFile = null;
        this.children = new ArrayList<FileInfo>();
    }

    public FileInfo(File me, FileInfo parentFile) throws FileNotFoundException {
        this.me = me;
        this.parentFile = parentFile;
        this.children = new ArrayList<FileInfo>();
        this.initMethods();
    }

    public File getMe() {
        return me;
    }

    public String getName() {
        return me.getName();
    }

    public FileInfo getParentFile() {
        return parentFile;
    }

    public void addChild(FileInfo child) {
        this.children.add(child);
    }

    public List<FileInfo> getChildren() {
        return children;
    }

    public boolean isDirectory() {
        return me.isDirectory();
    }

//    public String getType() {
//        if (me.isDirectory()) {
//            return "Директория";
//        } else {
//            return "Класс";
//        }
//    }

    public List<String> getMethods() {
        return this.methods;
    }

    private void initMethods() throws FileNotFoundException {
        this.methods = new ArrayList<String >();

        if (me.getPath().endsWith(".java")) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(this.me.getPath()));
            List<String> methodsNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodsNames);
            Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
            this.methods = methodsNames;
        }
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}
