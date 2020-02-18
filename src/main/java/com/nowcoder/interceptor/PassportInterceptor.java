package com.nowcoder.interceptor;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.Jwt.Jwt;
import com.nowcoder.util.Jwt.TokenState;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mffh on 2019/7/20
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket != null) {
            Map<String, Object> resultMap = Jwt.va1lidToken(ticket);
           /* String state = resultMap.get("state").toString();
            String d = TokenState.VALID.toString();
            boolean temp = d.equals(state)  ? true : false;*/
            if (resultMap == null || !TokenState.VALID.toString().equals(resultMap.get("state").toString())) {
                return true;
            }
            JWSObject jwsObject = JWSObject.parse(ticket);
            Payload payload = jwsObject.getPayload();
            JSONObject jsonOBJ = payload.toJSONObject();
            if(jsonOBJ.containsKey("uid")){
                int t = Integer.valueOf(jsonOBJ.get("uid").toString());
                User user = userDAO.selectById(t);
                hostHolder.setUser(user);
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
