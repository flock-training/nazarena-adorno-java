package Flock.Training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"Flock.Training.controllers", "Flock.Training.services", "Flock.Training.factories", "Flock.Training.security"})
@EntityScan("Flock.Training.models")
@EnableJpaRepositories("Flock.Training.repositories")
@ConfigurationPropertiesScan(basePackages = {"Flock.Training.config"})
public class TrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
    }
}
