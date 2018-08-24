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
package cn.indispensable.future.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * json工具类,对阿里巴巴的fastJSON进行包装
 * @author cicicc
 * @since 0.0.1
 */
public class JSONUtils {
    /**
     * 将数据打包为json格式并返回给服务器端
     * @param code 编号,常用的为1,表示添加失败
     * @param msg  一般用来存储失败信息
     * @return json格式的数据,返回给页面
     */
    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    /**
     * 接收map作为传输给前端的数据
     */
    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }
    /**
     * @param code 编号常用的为0和999,如果为999那么表示未登录,将会跳转到登录页面,如果是0的话就是添加成功,
     * @return json格式的数据,返回给页面
     */
    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }
}
