package cn.indispensable.future.async;

import java.util.List;

/**
 * handle的公共接口,所有handle必须实现EventHandle
 */
public interface EventHandler {

    //处理传递过来的model对象
    void doHandle(EventModel model);

    //获取当前handle所支持的事件类型
    List<EventType> getSupportEventTypes();

}
