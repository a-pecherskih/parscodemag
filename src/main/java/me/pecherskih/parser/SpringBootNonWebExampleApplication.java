package me.pecherskih.parser;

import me.pecherskih.parser.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class SpringBootNonWebExampleApplication
        implements CommandLineRunner {

    @Autowired
    private MainService mainService;

    public static void main(String[] args) {

        //отключаем баннер spring boot, если не хотим видеть его лого в консоли
        SpringApplication app = new SpringApplication(SpringBootNonWebExampleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    // В этом методе описываем нашу логику
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
           //System.out.println(args[0]);
        }
        mainService.run();
        exit(0); // завершаем программу
    }
}
