package com.zhanghan.grayapollo.config;

import com.zhanghan.grayapollo.properties.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

@Component
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @RefreshScope  //此注解用于动态刷新
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration rf = new RedisStandaloneConfiguration();
        rf.setDatabase(redisProperties.getDatabase());
        rf.setHostName(redisProperties.getHost());
        rf.setPort(redisProperties.getPort());
        rf.setPassword(RedisPassword.of(redisProperties.getPassword()));
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpb =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getMaxWait());
        jedisPoolConfig.setEvictorShutdownTimeoutMillis(redisProperties.getTimeout());
        jpb.poolConfig(jedisPoolConfig);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(rf, jpb.build());
        return jedisConnectionFactory;
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, String> strRedisTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(String.class));
        template.setValueSerializer(new GenericToStringSerializer<>(String.class));
        return template;
    }

    @Bean
    RedisTemplate<String, Long> longRedisTemplate() {
        final RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }

    @Bean
    RedisTemplate<String, Boolean> booleanRedisTemplate() {
        final RedisTemplate<String, Boolean> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Boolean.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Boolean.class));
        return template;
    }

    @Bean
    RedisTemplate<String, Integer> intRedisTemplate() {
        final RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Integer.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }
}