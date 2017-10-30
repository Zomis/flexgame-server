package net.zomis.spring.games;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.zomis.spring.games.generic.GroovyGames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    }

    @Bean
    public MongoClient mongoClient() {
        MongoClientURI uri = new MongoClientURI("mongodb://127.0.0.1:27017");
        return new MongoClient(uri);
    }

    @Bean
    public GroovyGames games() {
        return new GroovyGames();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:8080", "http://www.zomis.net", "http://gbg.zomis.net:8079");
            }
        };
    }

}
