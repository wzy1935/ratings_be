package com.sc.ratings.controllers;

import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.mappers.BoardMapper;
import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.services.BoardService;
import com.sc.ratings.services.UserService;
import com.sc.ratings.utils.AuthUtils;
import com.sc.ratings.utils.DataMap;
import com.sc.ratings.utils.RespData;
import com.sc.ratings.utils.authaop.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    AuthUtils authUtils;
    @Autowired
    BoardMapper boardMapper;

    public record BoardPT(String title, String description){}
    public record BoardIdPT(Integer board_id){}
    public record ModifyBoardPT(Integer board_id, String title, String description){}
    public record PageBoardPT(Integer page, Integer per_page, Integer user_id){}

    public DataMap getBoardData(BoardEntity board){
        DataMap boardData = DataMap.builder()
                .set("board_id", board.board_id())
                .set("title", board.title())
                .set("description", board.description())
                .set("overall_score", (float)(Math.round(board.overall_score()*10))/10)
                .set("scores", new Integer[]{board.score_1(),board.score_2(),board.score_3(),board.score_4(),board.score_5()})
                .set("score_cnt", board.score_1()+board.score_2()+board.score_3()+board.score_4()+board.score_5())
                .set("creator", DataMap.builder()
                        .set("user_id", board.creator_id())
                        .set("user_name", board.creator_name())
                );
        return boardData;
    }

    public List<DataMap> getListData(List<BoardEntity> boardList) {
        List<DataMap> resultList = new ArrayList<>();
        for (BoardEntity board : boardList) {
            DataMap boardData = getBoardData(board);
            resultList.add(boardData);
        }
        return resultList;
    }

    @GetMapping("api/board/get")
    public RespData getBoard(@ModelAttribute BoardIdPT pt) {
        var getBoardRT = boardService.getBoard(pt.board_id);
        if (getBoardRT.code().equals("SUCCESS")) {
            DataMap returnData = getBoardData(getBoardRT.board());
            return RespData.resp(getBoardRT.code(), returnData);
        }
        return RespData.resp(getBoardRT.code());
    }

    @Auth(type=Auth.Type.USER)
    @PostMapping("/api/board/create")
    public RespData createBoard(@RequestBody BoardController.BoardPT board){
        String code = boardService.createBoard(board.title(), board.description());
        return RespData.resp(code);
    }

    @Auth(type=Auth.Type.USER)
    @PostMapping("api/board/modify")
    public RespData modifyBoard(@RequestBody BoardController.ModifyBoardPT pt) {
        String code = boardService.modifyBoard(pt.board_id(),pt.title(),pt.description());
        return RespData.resp(code);
    }

    @Auth(type=Auth.Type.USER)
    @PostMapping("/api/board/delete")
    public RespData deleteBoard(@RequestBody BoardIdPT pt){
        String code = boardService.deleteBoard(pt.board_id());
        return RespData.resp(code);
    }


    @GetMapping("api/board/get-page")
    public RespData getPageBoard(@ModelAttribute PageBoardPT pt) {
        var getPageBoardRT = boardService.getPageBoard(pt.page(),pt.per_page(),pt.user_id());
        if (getPageBoardRT.code().equals("SUCCESS")) {
            var returnData = DataMap.builder()
                    .set("total_cnt", getPageBoardRT.totalCnt())
                    .set("list", getListData(getPageBoardRT.boardList())
                    );
            return RespData.resp(getPageBoardRT.code(), returnData);
        }
        return RespData.resp(getPageBoardRT.code());
    }






}
