package me.pecherskih.parser.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import me.pecherskih.parser.jsonModel.MyClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("test")
public class MainController {

    ArrayList<File> files = new ArrayList<File>();

    @GetMapping("/1")
    public void test1() throws Exception {
        File projectDir = new File("D:/0_Магистратура/PIbd21-Pecherskih-A.A_Java");

        explore(projectDir);

        List<MyClass> classes = new ArrayList<>();

        for (File file: files) {

            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file.getPath()));
            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);

            MyClass newClass = new MyClass(file.getName());
            newClass.setPackageName(file.getParent());
            newClass.setMethods(methodNames);

            classes.add(newClass);
        }

        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        objectMapper.writeValue(Paths.get("classes.json").toFile(), classes);
    }

    //
    private void explore(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(child);
            }
        } else if (file.getPath().endsWith(".java")) {
            files.add(file);
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
