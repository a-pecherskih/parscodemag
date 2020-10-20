package me.pecherskih.parser.jsonModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

@JsonIgnoreProperties({ "me", "parentFile" })
@JsonPropertyOrder({ "name", "type", "methods", "children" })
public class MyClass {
    private File me;
    private MyClass parentFile;

    private String name;
    private String type;

    private List<MyClass> children;
    private List<String> methods;

    public MyClass(File me) {
        this.me = me;
        this.parentFile = null;
        this.children = new ArrayList<MyClass>();
    }

    public MyClass(File me, MyClass parentFile) {
        this.me = me;
        this.parentFile = parentFile;
        this.children = new ArrayList<MyClass>();
    }

    public File getMe() {
        return me;
    }

    public void addChild(MyClass child) {
        this.children.add(child);
    }

    public MyClass getParentFile() {
        return parentFile;
    }

    public List<MyClass> getChildren() {
        return children;
    }

    public String getName() {
        return me.getName();
    }

    public String getType() {
        if (me.isDirectory()) {
            return "Директория";
        } else {
            return "Класс";
        }
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getMethods() throws FileNotFoundException {
        if (me.getPath().endsWith(".java")) {
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(this.me.getPath()));
            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);
            Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
            this.setMethods(methodNames);

            return methods;
        }
        return new ArrayList<String>();
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}
