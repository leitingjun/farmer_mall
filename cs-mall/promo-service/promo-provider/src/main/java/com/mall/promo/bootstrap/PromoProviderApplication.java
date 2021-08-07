package com.mall.promo.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.mall.promo")
@MapperScan("com.mall.promo.dal")
public class PromoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromoProviderApplication.class, args);
    }
}
