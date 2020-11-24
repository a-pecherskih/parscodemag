package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ParserService {

    public FileInfo parser(String path) {
        File dir = new File(path);
        //находим директорию src - отсюда начнется сравнение
        File file = this.findStartDirectory(dir, "src");

        File startDirectory = new File(file.getPath());
        FileInfo startDirectoryInfo = new FileInfo(startDirectory);

        //формируем дерево проекта с папки src (в children дочерние файлы и тп)
        this.explore(startDirectory, startDirectoryInfo);

        return startDirectoryInfo;
    }

    private File findStartDirectory(File file, String find) {
        if (file.getName().equals(find)) {
            return file;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                File found = this.findStartDirectory(f, find);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private void explore(File file, FileInfo parent) {
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
