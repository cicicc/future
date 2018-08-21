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
package cn.indispensable.future.async.handles;

import cn.indispensable.future.async.EventHandler;
import cn.indispensable.future.async.EventModel;
import cn.indispensable.future.async.EventType;

import java.util.List;

/**
 * @author cicicc
 * @since 0.0.1
 */
public class LoginHandler implements EventHandler {
    @Override
    public void doHandle(EventModel model) {
        
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return null;
    }
}
