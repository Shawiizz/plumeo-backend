package fr.shawiizz.plumeo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PlumeoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlumeoApplication.class, args);
    }

}
