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

import cn.indispensable.future.dao.MessageDAO;
import cn.indispensable.future.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    /**
     * 根据message的id获取message对象
     * @param id message的id
     * @return message对象,没有则为null
     */
    public Message getMessageById(int id){
        return messageDAO.selectMessageById(id);
    }

    /**
     * 根据message的id获取message对象
     * @param conversationId message的conversationId
     * @return message对象列表,没有则为null
     */
    public List<Message> getMessageByConversationId(String conversationId){
        return messageDAO.selectMessageByConversationId(conversationId);
    }

    /**
     * 添加消息
     * @return 是否添加成功(影响的数据数目)
     */
    public int addMessage(Message message) {

        return messageDAO.addMessage(message);
    }

    /**
     * 查询用户最近的会话列表
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 读取条数
     * @return List<Message>
     */
    public List<Message> getConversationList(int userId, int offset, int limit){

        return messageDAO.getConversationList(userId, offset, limit);
    }

    /**
     * 获取未读信息的数目
     * @param toId 当前用户id,只有发送给当前用户的信息才用计算未读数目
     * @param conversationId 用户会话的id
     * @return 未读信息数目
     */
    public int getUnreadMessageCount(int toId, String conversationId){

        return messageDAO.getUnreadMessageCount(toId, conversationId);
    }

    /**
     * 根据信息的id更改其状态为已读(DAO层将has_read设置为1)
     * @param id message的id
     */
    public void updateHasReadStatu(int id) {
        messageDAO.updateHasReadStatu(id);
    }
}
