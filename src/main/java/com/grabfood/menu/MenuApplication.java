package com.grabfood.menu;

import com.grabfood.menu.entity.Menu;
import com.grabfood.menu.repository.MenuRepository;
import java.math.BigDecimal;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class MenuApplication {
    public static void main(String[] args) { SpringApplication.run(MenuApplication.class, args); }
    @Bean CommandLineRunner seed(MenuRepository repository) {
        return args -> repository.findById(1L).orElseGet(() ->
                repository.save(new Menu(1L, "Phở Bò", new BigDecimal("60000"))));
    }
}
