package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HierarchyService {

    private int maxLevels;
    private int projectMaxDepth;
    private int javaLevel;

    private List<List<Integer>> levels;

    public HierarchyService() {
        this.maxLevels = 0;
        this.projectMaxDepth = 0;
        this.javaLevel = -999;
        this.levels = new ArrayList<>();
    }

    public float getResult(FileInfo project1, FileInfo project2) {
        int depthProject1 = this.getDepth(project1);
        int depthProject2 = this.getDepth(project2);

        this.maxLevels = Math.max(depthProject1, depthProject2);
        List<Integer> fill = new ArrayList<Integer>();
        for (int i = 0; i <= this.maxLevels; i++) {
            this.levels.add(i, fill);
        }

        //в приоритете проект с макс кол-вом уровней
        if (depthProject1 >= depthProject2) {
            System.out.println("В приоритете проект: " + project1.getMe().getPath());
            this.runAlgorithm(project1, project2, 0);
        } else {
            System.out.println("В приоритете проект: " + project2.getMe().getPath());
            this.runAlgorithm(project2, project1, 0);
        }

        return this.calcResult(maxLevels-1);
    }

    private float calcResult(int maxLevels) {
        System.out.println(this.levels);
        int numerator = 0;
        //если на уровне есть хотя бы одна 1 (различие на уровне), то плюсуем в числитель
        for (List<Integer> level : this.levels) {
            if (level.contains(1)) {
                numerator++;
            }
        }
        System.out.println("Уровней содержащие 1 (различия): " + numerator);
        System.out.println("Всего уровней: " + maxLevels);
        return (float) numerator/maxLevels;
    }

    /*
    Количество уровней в проекте
 */
    private int getDepth(FileInfo fileInfo) {
        this.projectMaxDepth = 0;
        this.calcDepth(fileInfo, 1);

        return this.projectMaxDepth;
    }

    private void calcDepth(FileInfo fileInfo, int depth) {
        if (depth > this.projectMaxDepth) {
            this.projectMaxDepth = depth;
        }
        for (FileInfo child : fileInfo.getChildren()) {
            if (child.isDirectory()) {
                this.calcDepth(child, depth+1);
            }
        }
    }

    private void runAlgorithm(FileInfo project1, FileInfo project2, int level) {
        //массив значений вершин [0,1] (файл не нейден - 1)
        List<Integer> evaluations = new ArrayList<Integer>();

        //проходим по дочерним директориям приоритетного проекта
        for (FileInfo child : project1.getChildren()) {
            //если есть директория java (следующие 3 уровня не сравниваются)
            if (child.getName().equals("java")) {
                this.javaLevel = level;
            }

            boolean isFind = false;

            // project2 = null, когда директория/файл не найдены в другом проекте
            if (project2 != null) {
                for (FileInfo child2 : project2.getChildren()) {
                    //есть ли директория/файл в другом проекте
                    if (child.getName().equals(child2.getName())) {
                        //если сравниваются директории, то сравниваем содержимое директорий
                        if (child.isDirectory()) {
                            this.runAlgorithm(child, child2, level+1);
                        }
                        isFind = true;
                    }
                }
            }
            if (!isFind) {
                this.runAlgorithm(child, null, level+1);

                //пропускаем 3 уровня после директории java (помечаем нулями оценки вершин)
                if (((level == this.javaLevel + 1) || (level == this.javaLevel + 2) || (level == this.javaLevel + 3))) {
                    evaluations.add(0);
                } else {
                    evaluations.add(1);
                }
            } else {
                evaluations.add(0);
            }
        }

        // объединяем оценки вершин одного уровня
        List<Integer> allEvaluations = new ArrayList<>(this.levels.get(level));
        allEvaluations.addAll(evaluations);
        this.levels.set(level, allEvaluations);
    }
}
