/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.indispensable.future.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * jedis工具类
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class JedisService implements InitializingBean {
    private final static Logger logger = LoggerFactory.getLogger(JedisService.class);
    private JedisPool jedisPool;
    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://localhost:6379/6");
    }

    public Long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("添加失败"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("添加失败"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("添加失败"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public boolean sismember(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("添加失败"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }
    public void lpush(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("添加失败"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public  List<String> brpop(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(0, key);
        } catch (Exception e) {
            logger.error("取出队列数据出现错误"+e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }



    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/6");
        jedis.append("name", "oldchen");
        System.out.println(jedis.get("name"));
        jedis.close();
    }
}
