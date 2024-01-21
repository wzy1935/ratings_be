package com.sc.ratings.services;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.RatingEntity;
import com.sc.ratings.mappers.BoardMapper;
import com.sc.ratings.mappers.RatingMapper;
import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sc.ratings.services.BoardService.checkPage;

@Service
public class RatingService {
    @Autowired
    RatingMapper ratingMapper;
    @Autowired
    BoardMapper boardMapper;
    @Autowired
    AuthUtils authUtils;
    @Autowired
    UserMapper userMapper;

    public record GetPageRatingRT(String code, Integer totalCnt, List<RatingEntity> ratingList){}

    static boolean checkScore(Integer score) {
        return score>=1&&score<=5;
    }
    static boolean checkDescription(String description) {
        return description.length() < 500;
    }


    //已测试
    public GetPageRatingRT getPageRating(Integer board_id, Integer page, Integer per_page) {
        List<RatingEntity> getList;
        if(boardMapper.getBoardById(board_id)!=null){
            getList = ratingMapper.getRatingsById(board_id);
        }
        else
            return new RatingService.GetPageRatingRT("NOT_EXIST",null,null);
        Integer size = getList.size();
        if (!checkPage(page,per_page,size))
            return new RatingService.GetPageRatingRT("INVALID",null,null);

        Integer start = (page - 1) * per_page;
        Integer end = Math.min(start+per_page,getList.size());
        List<RatingEntity> returnList = new ArrayList<>();
        for (int i=start;i<end;i++){
            returnList.add(getList.get(i));
        }
        Integer totalCnt = returnList.size();
        return new RatingService.GetPageRatingRT("SUCCESS",totalCnt,returnList);
    }

    //已测试
    public String createRating(Integer board_id,Integer score, String description) {
        if (!(checkScore(score) && checkDescription(description))) return "INVALID";
        String userName = authUtils.getCurrentUserName();
        Integer userId = userMapper.getUserByName(userName).id();
        BoardEntity board = boardMapper.getBoardById(board_id);
        if (board == null) return "INVALID";
        RatingEntity rating = ratingMapper.getRatingByBUId(board_id,userId);
        if (rating != null) return "ALREADY_EXIST";
        RatingEntity ratingNew = new RatingEntity(null,description,score,board_id,userId,userName);
        ratingMapper.addRating(ratingNew);
        //待改成数组
        Integer[] scoreNum = {board.score_1(),board.score_2(),board.score_3(),board.score_4(),board.score_5()};
        Integer scoreSum = 0;
        Integer userSum = 0;
        for (int i=0;i<5;i++){
            scoreSum+=(i+1)*scoreNum[i];
            if(score == i+1) scoreNum[i]+=1;
            userSum+=scoreNum[i];
        }
        Float averageScore = (float) ((scoreSum+score)/userSum);
        boardMapper.updateBoardScore(board_id,averageScore,scoreNum[0],scoreNum[1],scoreNum[2],scoreNum[3],scoreNum[4]);
        return "SUCCESS";
    }
}
