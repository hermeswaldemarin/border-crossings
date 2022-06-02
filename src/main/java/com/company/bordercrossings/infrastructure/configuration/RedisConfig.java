package com.company.bordercrossings.infrastructure.configuration;

import com.company.bordercrossings.domain.BorderCrossing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties properties) {
        return new LettuceConnectionFactory(properties.getRedisHost(), properties.getRedisPort());
    }

    @Bean
    public ReactiveHashOperations<String, String, BorderCrossing> hashOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory){
        var template = new ReactiveRedisTemplate<>(
                reactiveRedisConnectionFactory,
                RedisSerializationContext.<String, BorderCrossing>newSerializationContext(new StringRedisSerializer())
                                         .hashKey(new GenericToStringSerializer<>(String.class))
                                         .hashValue(new Jackson2JsonRedisSerializer<>(BorderCrossing.class))
                                         .build()
        );
        return template.opsForHash();
    }


}