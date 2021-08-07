package com.mall.shopping.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redissonconfig")
@Data
public class RedissonConfig {
    String url;

    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress(url);
        return Redisson.create(config);
    }
}
