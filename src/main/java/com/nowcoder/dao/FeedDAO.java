package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mffh on 2019/8/1
 */
@Repository
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type}"})
    int addFeed(Feed feed);


    @Select({"select count(id) from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed>selectUserFeeds(@Param("maxId")int maxId,
                              @Param("userIds")List<Integer>userIds,
                              @Param("count")int count);

}
