package com.sc.ratings.services;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.UserEntity;
import com.sc.ratings.exceptions.ForbiddenException;
import com.sc.ratings.exceptions.UnauthorizedException;
import com.sc.ratings.mappers.BoardMapper;
import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BoardService {
    @Autowired
    BoardMapper boardMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuthUtils authUtils;

    static boolean checkTitle(String title) {
        return title.length() < 100 && title.strip().length() > 0;
    }
    static boolean checkDescription(String description) {
        return description.length() < 500;
    }

    public static boolean checkPage(Integer page, Integer perPage, Integer size){
        if(page<=0 || perPage<=0)
            return false;
        if((page-1) * perPage > size) //待补充
            return false;
        return true;
    }

    private boolean isBoardCreatorOrAdmin(String creatorName){
        String userName = authUtils.getCurrentUserName();
        boolean isAdmin = userMapper.getUserByName(userName).is_admin();
//        System.out.println(userName);
//        System.out.println(isAdmin);
//        System.out.println(creatorName);
        if (isAdmin)  return true;
        if (userName.equals(creatorName))  return true;
        return false;
    }


    public record GetBoardRT(String code, BoardEntity board) {}

    public record GetPageBoardRT(String code, Integer totalCnt, List<BoardEntity> boardList){}
    //已测试
    public BoardService.GetBoardRT getBoard(Integer board_id) {
        if (board_id <= 0) return new BoardService.GetBoardRT("INVALID", null);
        BoardEntity board = boardMapper.getBoardById(board_id);
        if (board == null) {
            return new BoardService.GetBoardRT("NOT_EXIST", null);
        }
        return new BoardService.GetBoardRT("SUCCESS", board);
    }

    //已测
    public String createBoard(String title, String description) {
        if (!(checkTitle(title) && checkDescription(description))) return "INVALID";
        String userName = authUtils.getCurrentUserName();
        Integer userId = userMapper.getUserByName(userName).id();
        BoardEntity board = boardMapper.getBoardByTitle(title);
        if (board != null) return "ALREADY_EXIST";
        BoardEntity boardNew = new BoardEntity((Integer) null,title,description, (float) 0,0, 0, 0, 0, 0,userId,userName);
        boardMapper.addBoard(boardNew);
        return "SUCCESS";
    }

    //已测试
    public String modifyBoard(Integer board_id, String title, String description){
        if (!(checkTitle(title) && checkDescription(description))) return "INVALID";
        BoardEntity board = boardMapper.getBoardById(board_id);
        if(!isBoardCreatorOrAdmin(board.creator_name())) throw new ForbiddenException();
        if (board == null) return "NOT_EXIST";
        if(boardMapper.getBoardByTitle(title)!=null)
            return "ALREADY_EXIST";
        boardMapper.updateBoardById(board.board_id(),title,description);
        return "SUCCESS";

    }

    //已测试
    public String deleteBoard(Integer board_id){
        //INVALID待补充
        if (board_id <= 0) return "INVALID";
        BoardEntity board = boardMapper.getBoardById(board_id);
        if(!isBoardCreatorOrAdmin(board.creator_name())) throw new ForbiddenException();
        if (board == null) return "NOT_EXIST";
        boardMapper.deleteBoardById(board_id);
        return "SUCCESS";
    }

    //已测试
    public GetPageBoardRT getPageBoard(Integer page, Integer per_page, Integer user_id) {
        List<BoardEntity> getList;
        if (user_id==-1){
            getList = boardMapper.getBoardsByAll();
        }
        else {
            if (userMapper.getUserById(user_id) != null) {
                getList = boardMapper.getBoardsById(user_id);
                if (getList.size()==0)
                    return new GetPageBoardRT("NOT_EXIST", null, null);
            }
            else
                return new GetPageBoardRT("NOT_EXIST", null, null);
        }
        Integer size = getList.size();
        if (!checkPage(page,per_page,size))
            return new GetPageBoardRT("INVALID",null,null);

        Integer start = (page - 1) * per_page;
        Integer end = Math.min(start+per_page,getList.size());
        List<BoardEntity> returnList = new ArrayList<>();
        for (int i=start;i<end;i++){
            returnList.add(getList.get(i));
        }
        Integer totalCnt = getList.size();
        return new GetPageBoardRT("SUCCESS",totalCnt,returnList);
    }


}
