package me.pecherskih.parser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import me.pecherskih.parser.jsonModel.MyClass;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainService {

    private List<String> projectsPaths;

    public void run() throws Exception {
        File mainDir = new File("D:/0_Магистратура/javaparser");

        File file = this.searchPath(mainDir, "src");

        for (String path : this.projectsPaths) {
            File project = new File(path);
            MyClass mainClass = new MyClass(project);
            System.out.println("explore 2");
            this.explore(project, mainClass);

            ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
            objectMapper.writeValue(Paths.get(project.getName() + ".json").toFile(), mainClass);
        }
    }

    private File searchPath(File file, String search) {
        if (file.getName().equals(search)) {
            this.projectsPaths = new ArrayList<String>();
            for (File f : file.listFiles()) {
                this.projectsPaths.add(f.getPath());
            }
            return file;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                File found = searchPath(f, search);
                if (found != null)
                    return found;
            }
        }
        return null;
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
