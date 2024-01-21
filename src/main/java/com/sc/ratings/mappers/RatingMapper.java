package com.sc.ratings.mappers;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.RatingEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RatingMapper {
    @Select("select * from app_rating where board_id=#{board_id}")
    List<RatingEntity> getRatingsById(Integer board_id);

    @Insert("insert into app_rating (description, score, board_id, creator_id, creator_name) " +
            "values (#{description}, #{score}, #{board_id}, #{creator_id}, #{creator_name})")
    void addRating(RatingEntity ratingNew);

    @Select("select * from app_rating where board_id=#{board_id} and creator_id=#{creator_id}")
    RatingEntity getRatingByBUId(Integer board_id, Integer creator_id);
}
