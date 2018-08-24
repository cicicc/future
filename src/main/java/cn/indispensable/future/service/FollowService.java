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

import cn.indispensable.future.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 关注功能的service层
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class FollowService {
    @Autowired
    private JedisService jedisService;

    /**
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId 用户id
     * @param entityType 关注实体的类型
     * @param entityId 关注实体的id
     * @return 是否关注成功 Boolean值
     */
    public boolean follow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        long score = new Date().getTime();
        Jedis jedis = jedisService.getResource();
        Transaction transaction = jedisService.beginTransaction(jedis);
        // 实体的粉丝增加当前用户
        transaction.zadd(followerKey, score, String.valueOf(userId));
        // 当前用户对这类实体关注+1
        transaction.zadd(followeeKey, score, String.valueOf(entityId));
        List<Object> resultList = jedisService.exec(transaction, jedis);
        return resultList.size() == 2 && resultList.get(0) != null && resultList.get(1) != null;
    }


    /**
     * 用户取消关注了某个实体
     * @param userId 用户id
     * @param entityType 关注实体的类型
     * @param entityId 关注实体的id
     * @return 是否取消关注成功 Boolean值
     */
    public boolean unFollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        Jedis jedis = jedisService.getResource();
        Transaction transaction = jedisService.beginTransaction(jedis);
        // 实体的粉丝减去当前用户
        transaction.zrem(followerKey, String.valueOf(userId));
        // 当前用户对这类实体关注-1
        transaction.zrem(followeeKey, String.valueOf(entityId));
        List<Object> resultList = jedisService.exec(transaction, jedis);
        return resultList.size() == 2 && resultList.get(0) != null && resultList.get(1) != null;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        Set<String> set = jedisService.zrange(followerKey, 0, count);
        return FollowService.setToList(set);
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        Set<String> set = jedisService.zrange(followerKey, offset, limit);
        return FollowService.setToList(set);
    }

    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        Set<String> set = jedisService.zrange(followeeKey, 0, count);
        return FollowService.setToList(set);
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        Set<String> set = jedisService.zrange(followeeKey, offset, limit);
        return FollowService.setToList(set);
    }

    public boolean isFollower(int userId, int entityType, int entityId ) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        return jedisService.zscore(followerKey, String.valueOf(userId)) != null;
    }

    public Long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        return jedisService.zcard(followerKey);
    }
    public Long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        return jedisService.zcard(followeeKey);
    }


    private static List<Integer> setToList(Set<String> set) {
        List<Integer> list = new ArrayList<>();
        for (String element : set) {
            list.add(Integer.valueOf(element));
        }
        return list;
    }

}
