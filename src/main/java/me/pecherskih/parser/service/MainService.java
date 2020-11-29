package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class MainService {

    private ParserService parserService;
    private HierarchyService hierarchyService;

    public MainService(ParserService parserService, HierarchyService hierarchyService) {
        this.parserService = parserService;
        this.hierarchyService = hierarchyService;
    }

    public void run() throws FileNotFoundException {
        FileInfo project1 = parserService.parser("D:/0_Магистратура/javaparser");
        if (project1 == null) {
            System.out.println("Неверная структура проекта 1");
            return;
        }
        System.out.println("Начало сравнения проекта1 " + project1.getMe().getPath());

        FileInfo project2 = parserService.parser("D:/0_Магистратура/parscodemag");
        if (project2 == null) {
            System.out.println("Неверная структура проекта 2");
            return;
        }
        System.out.println("Начало сравнения проекта2 " + project2.getMe().getPath());

/*      write in json-file

        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        objectMapper.writeValue(Paths.get("project1.json").toFile(), project1);
*/

        float result = hierarchyService.getResult(project1, project2);
        System.out.println("Результат сравнения проекта 1 и проекта 2 = " + result);
        float result2 = hierarchyService.getResult(project2, project1);
        System.out.println("Результат сравнения проекта 2 и проекта 1 = " + result2);
    }
}
