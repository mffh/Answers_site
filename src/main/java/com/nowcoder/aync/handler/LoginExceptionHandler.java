package com.nowcoder.aync.handler;

import com.nowcoder.aync.EventHandler;
import com.nowcoder.aync.EventModel;
import com.nowcoder.aync.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mffh on 2019/8/4
 */

@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
         //判断发现用户登录异常
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"),"登录IP异常","mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LOGIN );
    }
}
