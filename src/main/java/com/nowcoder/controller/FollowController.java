package com.nowcoder.controller;

import com.nowcoder.aync.EventModel;
import com.nowcoder.aync.EventProducer;
import com.nowcoder.aync.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * Created by mffh on 2019/8/1
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;


    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer enventProducer;

    @Autowired
    SensitiveService sensitiveService;

    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("UserId") int userId) {
     if (hostHolder.getUser()==null){
         return WendaUtil.getJSONString(999);
     }
     boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId);
     enventProducer.fireEvent(new EventModel(EventType.FOLLOW)
             .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
             .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
      //返回关注的人数
     return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("UserId") int userId) {
        if (hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId);
        enventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        //返回关注的人数
        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        Question q=questionService.getById(questionId);
        if(q==null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }


        boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,questionId);
        enventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(q.getUserId()));

        Map<String,Object> info=new HashMap<String,Object>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret?0:1,info);
    }


    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        Question q=questionService.getById(questionId);
        if(q==null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }


        boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,questionId);
        enventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));
        Map<String,Object> info=new HashMap<String,Object>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret?0:1,info);

    }


    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model,
                            @PathVariable("uid") int userId) {

        List<Integer> followeeIds=followService.getFollowees(userId,EntityType.ENTITY_USER,0,10);
       if(hostHolder.getUser()!=null){
        model.addAttribute("followees",getUSersInfo(hostHolder.getUser().getId(),followeeIds));
       }else {
           model.addAttribute("followees",getUSersInfo(0,followeeIds));
       }
       return "followees";

    }
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model,
                            @PathVariable("uid") int userId) {

        List<Integer> followerIds=followService.getFollowers(userId,EntityType.ENTITY_USER,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followers",getUSersInfo(hostHolder.getUser().getId(),followerIds));
        }else {
            model.addAttribute("followers",getUSersInfo(0,followerIds));
        }
        return "followers";

    }



    private List<ViewObject> getUSersInfo(int localUserId,List<Integer>userIds){
        List<ViewObject>userInfos=new ArrayList<>();
        for(Integer uid:userIds){
            User user=userService.getUser(uid);
            if(user==null){
                continue;
            }
            ViewObject vo=new ViewObject();
            vo.set("user",user);
           vo.set("commentCount",commentService.getUserCommentCount(uid));
             vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            vo.set("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_USER,uid));
            if(localUserId !=0){
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,uid));
            }else{
                vo.set("followed",false);
            }
            userInfos.add(vo);
        }
        return  userInfos;

    }
}
