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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词的服务层
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class SensitiveService implements InitializingBean {

   //加载配置文件
    @Override
    public void afterPropertiesSet() throws Exception {
        
    }

    //设置默认敏感词替换符号
    private final static String DEFAULT_REPLACE_CONTENT = "***";

    //前缀树的节点
    private class TrieNode{
        //标记是否为敏感词结尾
        private boolean end = false;
        //该敏感字下的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        //查询当前指向的节点下面是否有key值为所查询值的子节点
        public TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        //判断是否为敏感词结尾
        boolean isKeyWordEnd() {
            return end;
        }

        //将当前的节点对应字设置为敏感词结尾或者设置为非敏感词结尾
        void setKeyWordEnd(Boolean end) {
            this.end = end;
        }

    }


}
