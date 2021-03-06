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

import cn.indispensable.future.dao.CommentDAO;
import cn.indispensable.future.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 处理评论中心的数据,并将包装好的数据返回给controller层
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private SensitiveService sensitiveService;


    public List<Comment> SelectLatestComment(int entityId, int entityType, int offset, int limit) {

        return commentDAO.selectLatestComments(entityId, entityType, offset, limit);
    }

    public Comment selectCommentById(int id) {

        return commentDAO.selectCommentById(id);
    }

    public int addComment(Comment comment) {
        comment.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(comment.getContent())));
        return commentDAO.addComment(comment);
    }

}
