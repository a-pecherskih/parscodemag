package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HierarchyService {

    private int projectMaxDepth;
    private List<List<Integer>> levels;

    public HierarchyService() {
        this.clear();
    }

    private void clear() {
        this.projectMaxDepth = 0;
        this.levels = new ArrayList<>();
    }

    public float getResult(FileInfo project1, FileInfo project2) {
        this.clear();

        int depthProject1 = this.getDepth(project1);

        List<Integer> emptyList = new ArrayList<Integer>();
        for (int i = 0; i <= depthProject1; i++) {
            this.levels.add(i, emptyList);
        }

        this.runAlgorithm(project1, project2, 0);

        System.out.println(this.levels);
        return this.calcResult(this.levels);
    }

    private float calcResult(List<List<Integer>> levels) {
        int countHeights = 0;
        int numerator = 0;
        //если на уровне есть хотя бы одна 1 (различие на уровне), то плюсуем в числитель
        for (List<Integer> level : levels) {
            for (Integer evaluation : level) {
                if (evaluation == 1) {
                    numerator++;
                }
                countHeights++;
            }
        }

        return (float) numerator/countHeights;
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
        //массив значений вершин [0,1] (файл не найден - 1)
        List<Integer> evaluations = new ArrayList<Integer>();
        List<String> projectExistFiles = new ArrayList<String>();
        //проходим по дочерним директориям приоритетного проекта
        for (FileInfo child : project1.getChildren()) {

            boolean isFind = false;

            // project2 = null, когда директория/файл не найдены в другом проекте
            if (project2 != null) {
                for (FileInfo child2 : project2.getChildren()) {
                    //есть ли директория/файл в другом проекте
                    if (child.getName().equals(child2.getName()) || (level == 0)) {
                        projectExistFiles.add(child.getName());
                        //если сравниваются директории, то сравниваем содержимое директорий
                        if (child.isDirectory()) {
                            this.runAlgorithm(child, child2, level+1);
                            isFind = true;
                        } else {
                            float resultCompareMethods = this.compareMethods(child, child2);
                            if ((resultCompareMethods < 0.2) || (Double.isNaN(resultCompareMethods))) {
                                isFind = true;
                            } else {
                                isFind = false;
                            }
                        }
                    }

                }
            }
            if (!isFind) {
                this.runAlgorithm(child, null, level+1);
                evaluations.add(1);
            } else {
                evaluations.add(0);
            }
        }

        List<Integer> diffEvaluations = new ArrayList<>();
        if (project2 != null) {
            for (FileInfo child2 : project2.getChildren()) {
                if (!projectExistFiles.contains(child2.getName())) {
                    if (child2.isDirectory()) {
                        int difference = this.countDiffFiles(child2);
                        for(int i = 0; i < difference; i++) {
                            diffEvaluations.add(1);
                        }
                    } else {
                        diffEvaluations.add(1);
                    }
                }
            }
        }

        // объединяем оценки вершин одного уровня
        List<Integer> allEvaluations = new ArrayList<>(this.levels.get(level));
        allEvaluations.addAll(evaluations);
        allEvaluations.addAll(diffEvaluations);
        this.levels.set(level, allEvaluations);
    }

    private int countDiffFiles(FileInfo dir) {
        int counter = 0;
        for (FileInfo child : dir.getChildren()) {
            if (child.isDirectory()) {
                counter+=this.countDiffFiles(child);
            } else {
                counter++;
            }
        }
        return counter;
    }

    private float compareMethods(FileInfo file1, FileInfo file2) {
        List<List<Integer>> levels = new ArrayList<>();
        //массив значений вершин [0,1] (метод не найден - 1)
        List<Integer> evaluations = new ArrayList<Integer>();

        for (String method1 : file1.getMethods()) {

            boolean isFind = false;

            for (String method2 : file2.getMethods()) {

                if (method1.equals(method2)) {
                    isFind = true;
                }
            }

            if (!isFind) {
                evaluations.add(1);
            } else {
                evaluations.add(0);
            }
        }

        levels.add(evaluations);

        return this.calcResult(levels);
    }

}
