package me.pecherskih.parser.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import me.pecherskih.parser.jsonModel.MyClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;

@RestController
@RequestMapping("test")
public class MainController {

    @GetMapping("/1")
    public void test1() throws Exception {
        File dir = new File("D:/0_Магистратура/PIbd21-Pecherskih-A.A_Java");

        MyClass mainClass = new MyClass(dir);
        this.explore(dir, mainClass);

        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        objectMapper.writeValue(Paths.get("new-classes.json").toFile(), mainClass);
    }

    private void explore(File file, MyClass parent) {
        MyClass current;
        if (file == parent.getMe()) {
            current = parent;
        } else {
            current = new MyClass(file, parent);
        }

        if (file.isDirectory() && !file.isHidden()) {
            if (file != parent.getMe()) {
                parent.addChild(current);
            }
            for (File child : file.listFiles()) {
                explore(child, current);
            }
        } else if (file.getPath().endsWith(".java")
        ) {
            if (file != parent.getMe()) {
                parent.addChild(current);
            }
        }
    }
}
