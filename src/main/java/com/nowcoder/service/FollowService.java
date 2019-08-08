package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by mffh on 2019/8/5
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /*
    用户关注了某个实体，可以关注问题，关注用户，关注评论等任何实体
    @Param userId
    @Param  entityType
    @Param entityId
    @return
    */
    public boolean follow(int userId,int entityType,int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        //实体的粉丝增加给当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
       //当前用户对这类实体关注+1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }
    /*
      取消关注
      @Param userId
    @Param  entityType
    @Param entityId
    @return
    */
    public boolean unfollow(int userId,int entityType,int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝减去当前用户
        tx.zrem(followerKey, String.valueOf(userId));
        // 当前用户对这类实体关注-1
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }
    private List<Integer>getIdsFromSet(Set<String> idset){
        List<Integer>ids=new ArrayList<>();
        for(String str:idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    public List<Integer> getFollowers(int entityType,int entityId,int count ){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count ){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevange(followerKey,offset,count));
    }

    public List<Integer> getFollowees(int entityType,int entityId,int count ){
        String followeeKey=RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int entityType,int entityId,int offset,int count ){
        String followeeKey=RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevange(followeeKey,offset,count));
    }

    public long getFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
       return jedisAdapter.zcard(followerKey);
    }
    public long getFolloweeCount(int entityType,int entityId){
        String followeeKey=RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return jedisAdapter.zcard(followeeKey);
    }
/*
判断用户是否关注了某个实体
@Param userId
    @Param  entityType
    @Param entityId
    @return
*/
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;

    }

}
