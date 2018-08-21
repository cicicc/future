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
package cn.indispensable.future.async;

import cn.indispensable.future.service.JedisService;
import cn.indispensable.future.utils.RedisKeyUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件的生产者,将事件发送到Redis中
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class EventProducer {
    @Autowired
    private JedisService jedisService;

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    /**
     * 将事件添加到Redis的队列中
     * @param model 封装了事件的model对象
     * @return 是否添加成功
     */
    public boolean fireEvent(EventModel model) {
        try {
            String json = JSON.toJSONString(model);
            String key = RedisKeyUtils.getEventQueueKey();
            jedisService.lpush(key, json);
            return true;
        } catch (Exception e) {
            logger.error("事件添加失败"+e.getMessage());
            return false;
        }
    }
}
