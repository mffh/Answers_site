package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.IndexController;
import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by mffh on 2019/8/2
 */
@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;
    public  static void print(int index,Object obj){
        System.out.println(String.format("%d, %s",index,obj.toString()));
    }
    public static void main(String[]argv){
        Jedis jedis=new Jedis("redis://localhost:6379/15");
        jedis.flushDB();
        //getset
        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        jedis.setex("hello2",15,"world");


        //自增自减
        jedis.set("pv","100");
        jedis.incr("pv");
        print(2,jedis.get("pv"));
        jedis.incrBy("pv",5);
        print(2,jedis.get("pv"));
        jedis.decrBy("pv",2);
        print(2,jedis.get("pv"));

        //redis里的正则表达式
        print(3,jedis.keys("*"));

        //list
/*
        String listName="list";
        jedis.del(listName);
        for(int i=0;i<10;++i){
            jedis.lpush(listName,"a"+String.valueOf(i));
        }
        print(4,jedis.lrange(listName,0,12));
        print(4,jedis.lrange(listName,0,3));
        print(4,jedis.llen(listName));
        print(4,jedis.lpop(listName));
        print(4,jedis.llen(listName));
        print(4,jedis.lrange(listName,2,6));
        print(4,jedis.lindex(listName,3));
        print(4,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));
        print(4,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","bb"));
        print(4,jedis.lrange(listName,0,-1));
*/

       //hssh
   /*     String userKey="userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","8888888");
        print(5,jedis.hget(userKey,"name"));
        print(5,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(5,jedis.hgetAll(userKey));
        print(5,jedis.hexists(userKey,"email"));
        print(5,jedis.hexists(userKey,"age"));
        print(5,jedis.hkeys(userKey));
        print(5,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"school","zju");
        jedis.hsetnx(userKey,"name","yxy");
        print(5,jedis.hgetAll(userKey));
   */
        //set
       String listKey1 = "commentLike1";
        String listKey2 = "commentLike2";
        for(int i=0;i<10;i++){
            jedis.sadd(listKey1,String.valueOf(i));
            jedis.sadd(listKey2,String.valueOf(i*i));
        }
        print(6,jedis.smembers(listKey1));
        print(6,jedis.smembers(listKey2));
        //两个集合的并集
        print(6,jedis.sunion(listKey1,listKey2));
        //第一个集合有，第二个集合没有的
        print(6,jedis.sdiff(listKey1,listKey2));
        //两个集合的交集
        print(6,jedis.sinter(listKey1,listKey2));
        //集合里面是否存在，返回true或false
        print(6,jedis.sismember(listKey1,"12"));
        print(6,jedis.sismember(listKey1,"9"));
        //删除
        jedis.srem(listKey1,"5");
        print(6,jedis.smembers(listKey1));
        //将listKey2中的25挪到listkey1
        jedis.smove(listKey2,listKey1,"25");
        print(6,jedis.smembers(listKey1));
        print(6,jedis.smembers(listKey2));
        //有集合有多少个数值
        print(6,jedis.scard(listKey1));

        //
        String rankKey="rankKey";
        jedis.zadd(rankKey,15,"jim");
        jedis.zadd(rankKey,60,"le");
        jedis.zadd(rankKey,65,"ru");
        jedis.zadd(rankKey,66,"si");
        jedis.zadd(rankKey,100,"qi");
        print(7,jedis.zcard(rankKey));
        print(7,jedis.zcount(rankKey,61,100));
        print(7,jedis.zscore(rankKey,"le"));
        jedis.zincrby(rankKey,2,"le");
        print(7,jedis.zscore(rankKey,"le"));
        jedis.zincrby(rankKey,2,"led");
        print(7,jedis.zscore(rankKey,"led"));
        print(7,jedis.zrange(rankKey,0,100));
        print(7,jedis.zrange(rankKey,0,10));
        print(7,jedis.zrange(rankKey,1,3));
        print(7,jedis.zrevrange(rankKey,1,3));
        for(Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,60,100)){
            print(7,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }
        print(7,jedis.zrevrank(rankKey,"le"));
        print(7,jedis.zrank(rankKey, "le"));

        //分数一样, 默认根据字典序排序,此时自动将c放在d前
        String setKey="zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"f");
        //"-"号可以看成负无穷，"+"正无穷
        print(8,jedis.zlexcount(setKey,"-","+"));
       //包含与不包含
        print(8,jedis.zlexcount(setKey,"[b","[d"));
        print(8,jedis.zlexcount(setKey,"(b","[d"));
        //删除
        jedis.zrem(setKey,"b");
        //正反输出
        print(8,jedis.zrange(setKey,0,10));
        print(8,jedis.zrevrange(setKey,0,10));
        //删除某个元素往后
        jedis.zremrangeByLex(setKey,"(c","+");
        print(8,jedis.zrange(setKey,0,2));

        //链接池
//        JedisPool pool=new JedisPool();
//        for(int i=0;i<10;++i){
//            Jedis j=pool.getResource();
//            print(12,j.get("pv"));
//           j.close();
//        }

        User user=new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(13,JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        String value=jedis.get("user1");
        User user2= JSON.parseObject(value,User.class);
        print(13,user2);
    }




    @Override
    public void afterPropertiesSet()throws Exception{
        pool=new JedisPool("redis://localhost:6379/10");
    }
    public long sadd(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
           return  jedis.sadd(key,value);
        }catch (Exception e){
           logger.error("发送异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public long srem(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发送异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发送异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public boolean sismember(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        } catch (Exception e) {
            logger.error("发送异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long lpush(String key,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout , String key){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }


    public Jedis getJedis(){
        return pool.getResource();
    }
    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        return null;
    }

    public List<Object>exec(Transaction tx,Jedis jedis){
        try{
            return tx.exec();
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(tx!=null){
                try {
                tx.close();
            }catch (IOException ioe){
                    logger.error("发生异常"+ioe.getMessage());
                }
        }
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key,double score,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zadd(key,score,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrevange(String key, int  start, int end){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zrevrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    public long zcard(String key){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public Double zscore(String key,String member){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zscore(key,member);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }


    public List<String> lrange(String key,int start,int end){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.lrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }



}
