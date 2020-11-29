package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParserService {

    public FileInfo parser(String path) throws FileNotFoundException {
        File dir = new File(path);
        //находим директорию src - отсюда начнется поиск директории с .java файлом
        File src = this.findSrc(dir, "src");
        File startDirectory = this.findStartDir(src);

        if (startDirectory == null) {
            return null;
        }

        FileInfo startDirectoryInfo = new FileInfo(new File(startDirectory.getPath()));

        //формируем дерево проекта с папки src (в children дочерние файлы и тп)
        this.explore(startDirectory, startDirectoryInfo);

        return startDirectoryInfo;
    }

    private File findSrc(File file, String find) {
        if (file.getName().equals(find)) {
            return file;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                File found = this.findSrc(f, find);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private File findStartDir(File parent) {

        List<File> childrenExistJava = new ArrayList<File>();
        List<File> children = new ArrayList<File>();
        for (File child : parent.listFiles()) {
            if (child.getName().equals("test") || child.getName().equals("resources")) {
                continue;
            }
            if (child.isDirectory()) {
                children.add(child);
                if (this.existJavaClass(child)) {
                    childrenExistJava.add(child);
                }
            }
        }

        if (childrenExistJava.size() == 1) {
            return childrenExistJava.get(0);
        } else if (childrenExistJava.size() > 1) {
            return parent;
        } else if (childrenExistJava.isEmpty()) {
            return this.findStartDir(children.get(0));
        }
        return null;
    }

    private boolean existJavaClass(File file) {
        for (File f : file.listFiles()) {
            if (f.getPath().endsWith(".java")) {
                return true;
            }
        }

        return false;
    }

    private void explore(File file, FileInfo parent) throws FileNotFoundException {
        FileInfo current;
        if (file == parent.getMe()) {
            current = parent;
        } else {
            current = new FileInfo(file, parent);
        }

        if (file.isDirectory() && !file.isHidden()) {
            if (file != parent.getMe()) {
                parent.addChild(current);
            }
            for (File child : file.listFiles()) {
                explore(child, current);
            }
        } else if (file.getPath().endsWith(".java")) {
            if (file != parent.getMe()) {
                parent.addChild(current);
            }
        }
    }
}
