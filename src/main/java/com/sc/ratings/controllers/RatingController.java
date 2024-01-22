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
import org.springframework.web.bind.annotation.*;

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
    public record RatingIdPT(Integer rating_id){}
    public record ModifyRatingPT(Integer rating_id, Integer score, String description){}
    public record GetUserRatingPT(Integer user_id,Integer board_id){}

    @GetMapping("api/rating/get-page")
    public RespData getPageRating(@ModelAttribute PageRatingPT pt) {
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

    @GetMapping("api/rating/get")
    public RespData getRating(@ModelAttribute RatingIdPT pt) {
        var getRatingRT = ratingService.getRating(pt.rating_id);
        if (getRatingRT.code().equals("SUCCESS")) {
            DataMap returnData = getRatingData(getRatingRT.rating());
            return RespData.resp(getRatingRT.code(), returnData);
        }
        return RespData.resp(getRatingRT.code());
    }

    @GetMapping("api/rating/get-user")
    public RespData getUserRating(@ModelAttribute GetUserRatingPT pt) {
        var getRatingRT = ratingService.getUserRating(pt.user_id, pt.board_id);
        if (getRatingRT.code().equals("SUCCESS")) {
            DataMap returnData = getRatingData(getRatingRT.rating());
            return RespData.resp(getRatingRT.code(), returnData);
        }
        return RespData.resp(getRatingRT.code());
    }

    @Auth(type = Auth.Type.USER)
    @PostMapping("api/rating/modify")
    public RespData modifyRating(@RequestBody ModifyRatingPT pt){
        String code = ratingService.modifyRating(pt.rating_id(),pt.score(),pt.description());
        return RespData.resp(code);
    }

    @Auth(type = Auth.Type.USER)
    @PostMapping("api/rating/delete")
    public RespData deleteRating(@RequestBody RatingIdPT pt){
        String code = ratingService.deleteRating(pt.rating_id());
        return RespData.resp(code);
    }

}
