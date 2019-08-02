package com.nowcoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by mffh on 2019/7/15
 */
@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}
