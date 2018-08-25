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

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词的服务层
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);



    //设置默认敏感词替换符号
    private final static String DEFAULT_REPLACE_CONTENT = "*";

    //前缀树的节点
    private class TrieNode{
        //标记是否为敏感词结尾
        private boolean end = false;
        //该敏感字下的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        //添加子节点
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }
        //查询当前指向的节点下面是否有key值为所查询值的子节点
        TrieNode getSubNode(Character key) {
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
        //获取当前节点的子节点数目
        int getSubNodeCount() {
            return subNodes.size();
        }

    }

    //创建一个根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    //加载配置文件,敏感词文件
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineText;
            while ((lineText = bufferedReader.readLine()) != null) {
                lineText = lineText.trim();
                addWord(lineText);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    /**
     * 将敏感词添加到敏感词所构成的前缀树中
     * @param lineText 读取到的敏感词
     */
    private void addWord(String lineText) {
        //创建一个指针指向根节点
        TrieNode templateTrieNode = rootNode;
        //循环传递过来的文本内容 将数据添加到前缀树
        for (int i = 0; i < lineText.length(); i++) {
            Character c = lineText.charAt(i);
            //如果当前循环到的字符不是中英文字符,那么就跳过这个字符
            if (isSymbol(c)) {
                continue;
            }
            //试图寻找子节点是否包括现在遍历的节点
            TrieNode subNode = templateTrieNode.getSubNode(c);
            if (subNode==null){
                //如果不存在此节点的话,就新建一个
                subNode = new TrieNode();
                templateTrieNode.addSubNode(c, subNode);
            }
            //继续向下进行延伸
            templateTrieNode = subNode;
            //判断是否已经到了敏感词结尾,如果到了结尾的话就将其end标签设为true
            if (i == lineText.length() - 1) {
                templateTrieNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 要过滤的文本
     * @return 过滤以后的结果
     */
    public String filter(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        TrieNode templateNode = rootNode;
        //index和concurrentPosition是用于当前已过滤文本和当前过滤到的位置的标记
        int begin = 0;//用于标记回滚的位置
        int concurrentPosition = 0;
        StringBuilder resultText = new StringBuilder();
        while (concurrentPosition < text.length()) {
            char c = text.charAt(concurrentPosition);
            //首先判断该字是否为特殊文字
            if (isSymbol(c)) {
                if (templateNode == rootNode) {
                    resultText.append(c);
                    ++begin;
                }
                ++concurrentPosition;
                continue;
            }
            TrieNode subNode = templateNode.getSubNode(c);
            if (subNode == null) {
                //当前遍历的节点并不存在敏感词,结束本位置的查询
                resultText.append(text.charAt(begin));
                templateNode = rootNode;
                begin = begin+1;
                concurrentPosition = begin;
            } else if (subNode.isKeyWordEnd()) {
                //如果当前指向的节点为结束节点的话,就将其修改为替代词
                for (int j = 0; j <= concurrentPosition - begin; j++) {
                    resultText.append(DEFAULT_REPLACE_CONTENT);
                }
                templateNode = rootNode;
                begin = concurrentPosition + 1;
                concurrentPosition++;
            }else{
                //当前节点在敏感词中存在并且不是结束节点
                templateNode = subNode;
                concurrentPosition++;
            }
        }


        return resultText.toString();
    }

}
