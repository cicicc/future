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

import cn.indispensable.future.utils.JedisUtils;
import cn.indispensable.future.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * 点赞的service层
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class LikeService {
    @Autowired
    private JedisUtils jedisUtils;

    /**
     * 对某回答表示喜欢
     */
    public Long addLike(int userId, int entityType, int entityId) {
        return likeOrDislike(userId, entityType, entityId, true);
    }

    /**
     * 对某回答表示不喜欢
     */
    public Long addDislike(int userId, int entityType, int entityId) {
        return likeOrDislike(userId, entityType, entityId, false);
    }

    /**
     * 获取点击喜欢的人的数目
     * @return 点击喜欢的人的数目
     */
    public Long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        return jedisUtils.scard(likeKey);
    }

    /**
     * 获取用户是否点了喜欢,不喜欢
     * @return 未点击返回0,点击喜欢返回1,点击不喜欢返回-1
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtils.getDislikeKey(entityType, entityId);
        return jedisUtils.sismember(likeKey, String.valueOf(userId)) ? 1 : jedisUtils.sismember(dislikeKey, String.valueOf(userId)) ? -1 : 0;

    }

    /**
     * 抽取了一下喜欢和不喜欢的两个方法的公有部分
     * 如果标记为喜欢(like),则like=true
     */
    private Long likeOrDislike(int userId, int entityType, int entityId, boolean like){
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtils.getDislikeKey(entityType, entityId);
        if (like) {
            jedisUtils.sadd(likeKey, String.valueOf(userId));
            if (jedisUtils.sismember(dislikeKey, String.valueOf(userId))) {
                jedisUtils.srem(dislikeKey, String.valueOf(userId));
            }
        }else{
            jedisUtils.sadd(dislikeKey, String.valueOf(userId));
            if (jedisUtils.sismember(likeKey, String.valueOf(userId))) {
                jedisUtils.srem(likeKey, String.valueOf(userId));
            }
        }
        return jedisUtils.scard(likeKey);
    }

}
