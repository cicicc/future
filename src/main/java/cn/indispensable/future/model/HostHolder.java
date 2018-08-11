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

import org.springframework.stereotype.Component;

/**
 * 对当前连接服务器的user对象提供处理的类
 * @author cicicc
 * @since 0.0.1
 */
@Component
public class HostHolder {
    //为每一个线程创建属于这个线程的user数据
    private static ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 获取当前线程的user对象
     */
    public  User getUser() {
        return users.get();
    }

    /**
     * 为当前线程设置user对象
     */
    public  void setUsers(User user) {
        users.set(user);
    }

    /**
     * 移除当前线程的user对象
     */
    public  void clear() {
        users.remove();
    }

}
