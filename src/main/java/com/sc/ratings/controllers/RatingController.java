package com.sc.ratings.controllers;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.RatingEntity;
import com.sc.ratings.mappers.BoardMapper;
import com.sc.ratings.mappers.RatingMapper;
import com.sc.ratings.services.BoardService;
import com.sc.ratings.services.RatingService;
import com.sc.ratings.utils.AuthUtils;
import com.sc.ratings.utils.DataMap;
import com.sc.ratings.utils.RespData;
import com.sc.ratings.utils.authaop.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RatingController {
    @Autowired
    AuthUtils authUtils;
    @Autowired
    RatingMapper ratingMapper;
    @Autowired
    RatingService ratingService;

    public DataMap getRatingData(RatingEntity rating){
        DataMap ratingData = DataMap.builder()
                .set("rating_id", rating.rating_id())
                .set("description", rating.description())
                .set("score", rating.score())
                .set("creator", DataMap.builder()
                        .set("user_id", rating.creator_id())
                        .set("user_name", rating.creator_name())
                );
        return ratingData;
    }

    public List<DataMap> getRatingListData(List<RatingEntity> ratingList) {
        List<DataMap> resultList = new ArrayList<>();
        for (RatingEntity rating : ratingList) {
            DataMap ratingData = getRatingData(rating);
            resultList.add(ratingData);
        }
        return resultList;
    }

    public record PageRatingPT(Integer board_id, Integer page, Integer per_page){}
    public record RatingPT(Integer board_id,Integer score, String description){}

    @GetMapping("api/rating/get-page")
    public RespData getPageRating(@RequestBody PageRatingPT pt) {
        var getPageRatingRT = ratingService.getPageRating(pt.board_id(),pt.page(),pt.per_page());
        if (getPageRatingRT.code().equals("SUCCESS")) {
            var returnData = DataMap.builder()
                    .set("total_cnt", getPageRatingRT.totalCnt())
                    .set("list", getRatingListData(getPageRatingRT.ratingList())
                    );
            return RespData.resp(getPageRatingRT.code(), returnData);
        }
        return RespData.resp(getPageRatingRT.code());
    }

    @Auth(type=Auth.Type.USER)
    @PostMapping("/api/rating/create")
    public RespData createBoard(@RequestBody RatingPT rating){
        String code = ratingService.createRating(rating.board_id(),rating.score(), rating.description());
        return RespData.resp(code);
    }

}
