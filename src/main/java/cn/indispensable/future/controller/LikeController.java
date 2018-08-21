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
package cn.indispensable.future.controller;

import cn.indispensable.future.async.EventModel;
import cn.indispensable.future.async.EventProducer;
import cn.indispensable.future.async.EventType;
import cn.indispensable.future.model.Comment;
import cn.indispensable.future.model.EntityType;
import cn.indispensable.future.model.HostHolder;
import cn.indispensable.future.model.User;
import cn.indispensable.future.service.CommentService;
import cn.indispensable.future.service.LikeService;
import cn.indispensable.future.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cicicc
 * @since 0.0.1
 */
@Controller
public class LikeController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);


    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId")int commentId) {
        User currentUser = hostHolder.getUser();
        try {
            if (currentUser == null) {
                return JSONUtils.getJSONString(999);
            }
            Long likeCount = likeService.addLike(currentUser.getId(), EntityType.ENTITY_COMMENT, commentId);
            Comment comment = commentService.selectCommentById(commentId);
            eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_COMMENT).setType(EventType.LIKE)
                    .setEntityOwnerId(comment.getUserId()).setEntityId(commentId)
                    .addExt("questionId", String.valueOf(comment.getEntityId())));
            return JSONUtils.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("喜欢答案出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "赞同此答案出错");
        }
    }

    @RequestMapping(value = "/dislike", method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam("commentId")int commentId) {
        User currentUser = hostHolder.getUser();
        try {
            if (currentUser == null) {
                return JSONUtils.getJSONString(999);
            }
            Long likeCount = likeService.addDislike(currentUser.getId(), EntityType.ENTITY_COMMENT, commentId);
            return JSONUtils.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("反对答案出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "反对此答案出错");
        }
    }
}
