package com.sc.ratings.entities;

public record RatingEntity(
        Integer rating_id,
        String description,
        Integer score,
        Integer board_id,
        Integer creator_id,
        String creator_name
){}
