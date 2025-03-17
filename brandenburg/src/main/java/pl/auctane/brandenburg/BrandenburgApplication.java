package pl.auctane.brandenburg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BrandenburgApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrandenburgApplication.class, args);
    }

}
