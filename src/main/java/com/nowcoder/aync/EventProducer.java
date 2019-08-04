package com.nowcoder.aync;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mffh on 2019/8/4
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public  boolean fireEvent(EventModel eventModel){
        try {
            String json= JSONObject.toJSONString(eventModel);
            String key= RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
