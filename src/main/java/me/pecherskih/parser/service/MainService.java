package me.pecherskih.parser.service;

import me.pecherskih.parser.model.FileInfo;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    private ParserService parserService;
    private HierarchyService hierarchyService;

    public MainService(ParserService parserService, HierarchyService hierarchyService) {
        this.parserService = parserService;
        this.hierarchyService = hierarchyService;
    }

    public void run() throws Exception {
        FileInfo project1 = parserService.parser("D:/0_Магистратура/javaparser");
        FileInfo project2 = parserService.parser("D:/0_Магистратура/javaparser");

/*      write in json-file

        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        objectMapper.writeValue(Paths.get("project1.json").toFile(), project1);
*/

        float result = hierarchyService.getResult(project1, project2);
        System.out.println("Результат выполнения: " + result);
    }











}
