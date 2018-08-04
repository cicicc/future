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
package cn.indispensable.future.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 将要传递给视图的对象封装到这个类的对象中,然后将这个类封装到model对象中
 * 把数据传递给页面 这样做的好处是可以减少传递给页面的对象
 * @author cicicc
 * @since 0.0.1
 */
public class ViewObject {
    private Map<String, Object> objects = new HashMap<>();

    public Object get(String key) {
        return objects.get(key);
    }

    public void put(String key, Object value) {
        objects.put(key, value);
    }
}
