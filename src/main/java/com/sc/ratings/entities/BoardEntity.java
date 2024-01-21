package com.sc.ratings.entities;
import org.apache.ibatis.type.*;

public record BoardEntity(
        Integer board_id,
        String title,
        String description,
        Float overall_score,
//        Integer[] scores,
//        UserEntity creator
        Integer score_1,Integer score_2,Integer score_3,Integer score_4,Integer score_5,
        Integer creator_id,
        String creator_name){}

