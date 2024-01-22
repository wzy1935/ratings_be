package com.sc.ratings.services;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.RatingEntity;
import com.sc.ratings.exceptions.UnauthorizedException;
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
    public record GetRatingRT(String code, RatingEntity rating){}


    private boolean isRatingCreatorOrAdmin(String creatorName){
        String userName = authUtils.getCurrentUserName();
        boolean isAdmin = userMapper.getUserByName(userName).is_admin();
        if (isAdmin || userName.equals(creatorName))  return true;
        return false;
    }

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
        Integer[] scoreCnt = {board.score_1(),board.score_2(),board.score_3(),board.score_4(),board.score_5()};
        Integer scoreSum = 0;
        Integer userCnt = 0;
        for (int i=0;i<5;i++){
            scoreSum+=(i+1)*scoreCnt[i];
            if(score == i+1) scoreCnt[i]+=1;
            userCnt+=scoreCnt[i];
        }
        Float average =  ((scoreSum+score)/(float)userCnt);
        boardMapper.updateBoardScore(board_id,average,scoreCnt[0],scoreCnt[1],scoreCnt[2],scoreCnt[3],scoreCnt[4]);
        return "SUCCESS";
    }

    //已测试
    public GetRatingRT getRating(Integer ratingId) {
        //INVALID
        RatingEntity rating = ratingMapper.getRatingById(ratingId);
        if (rating == null) {
            return new GetRatingRT("NOT_EXIST", null);
        }
        return new GetRatingRT("SUCCESS", rating);
    }

    public String modifyRating(Integer ratingId, Integer score, String description) {
        if(!(checkScore(score) && checkDescription(description))) return "INVALID";
        RatingEntity rating = ratingMapper.getRatingById(ratingId);
        if(score.equals(rating.score())) return "INVALID";
        if(!isRatingCreatorOrAdmin(rating.creator_name())) throw new UnauthorizedException();
        if(rating == null) return "NOT_EXIST";
        ratingMapper.updateRatingById(ratingId,score,description);
        BoardEntity board = boardMapper.getBoardById(rating.board_id());
        Integer[] scoreCnt = {board.score_1(),board.score_2(),board.score_3(),board.score_4(),board.score_5()};
        Integer scoreSum = 0;
        Integer userCnt = 0;
        Integer oldScore = rating.score();
        for (int i=0;i<5;i++){
            scoreSum+=(i+1)*scoreCnt[i];
            if(score == i+1) scoreCnt[i]+=1;
            if(oldScore == i+1) scoreCnt[i]-=1;
            userCnt+=scoreCnt[i];
        }
        Float average =  ((scoreSum+score-oldScore)/(float)userCnt);
        boardMapper.updateBoardScore(rating.board_id(),average,scoreCnt[0],scoreCnt[1],scoreCnt[2],scoreCnt[3],scoreCnt[4]);
        return "SUCCESS";
    }

    public String deleteRating(Integer ratingId){
        if (ratingId <= 0) return "INVALID";
        RatingEntity rating = ratingMapper.getRatingById(ratingId);
        if(!isRatingCreatorOrAdmin(rating.creator_name())) throw new UnauthorizedException();
        if (rating == null) return "NOT_EXIST";
        ratingMapper.deleteRatingById(ratingId);
        BoardEntity board = boardMapper.getBoardById(rating.board_id());
        Integer[] scoreCnt = {board.score_1(),board.score_2(),board.score_3(),board.score_4(),board.score_5()};
        Integer scoreSum = 0;
        Integer userCnt = 0;
        Integer oldScore = rating.score();
        for (int i=0;i<5;i++){
            scoreSum+=(i+1)*scoreCnt[i];
            if(oldScore == i+1) scoreCnt[i]-=1;
            userCnt+=scoreCnt[i];
        }
        Float average = ((scoreSum-oldScore)/(float)userCnt);
        boardMapper.updateBoardScore(rating.board_id(),average,scoreCnt[0],scoreCnt[1],scoreCnt[2],scoreCnt[3],scoreCnt[4]);
        return "SUCCESS";
    }

    public GetRatingRT getUserRating(Integer userId, Integer boardId){
        RatingEntity rating = ratingMapper.getRatingByBUId(boardId,userId);
        if (rating == null) {
            return new GetRatingRT("NOT_EXIST", null);
        }
        return new GetRatingRT("SUCCESS", rating);

    }


}
