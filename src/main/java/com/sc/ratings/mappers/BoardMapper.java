package com.sc.ratings.mappers;
import com.sc.ratings.entities.BoardEntity;
import com.sc.ratings.entities.UserEntity;
import org.apache.ibatis.annotations.*;
import com.sc.ratings.utils.IntegerArrayTypeHandler;
import org.apache.ibatis.type.ArrayTypeHandler;
import java.util.List;

@Mapper
public interface BoardMapper {
    @Select("select * from app_board where board_id=#{board_id}")
//    @Result(column = "scores", property = "scores", typeHandler = ArrayTypeHandler.class)
    BoardEntity getBoardById(Integer board_id);

    @Select("select * from app_board where title=#{title}")
//    @Results({
//    @Result(property = "scores", column = "scores", typeHandler = ArrayTypeHandler.class)
//    })
    BoardEntity getBoardByTitle(String title);

//    @Insert("insert into app_board (title, description, overall_score, scores, creator_id, creator_name) values (#{title}, #{description}, #{overall_score}, #{scores,jdbcType=ARRAY ,typeHandler=org.apache.ibatis.type.ArrayTypeHandler}, #{creator_id}, #{creator_name})")
    @Insert("insert into app_board (title, description, overall_score, score_1,score_2,score_3,score_4,score_5, creator_id, creator_name) " +
        "values (#{title}, #{description}, #{overall_score}, #{score_1},#{score_2},#{score_3},#{score_4},#{score_5}, #{creator_id}, #{creator_name})")
    void addBoard(BoardEntity board);

//    @Update("update app_user set name=#{name},password=#{password},is_admin=#{is_admin} where id =#{id}")
    @Update("update app_board set title = #{title}, description = #{description} WHERE board_id = #{board_id}")
    void updateBoardById(Integer board_id, String title, String description);

    @Delete("delete from app_board where board_id = #{board_id}")
    void deleteBoardById(Integer board_id);

    @Select("select * from app_board where creator_id=#{user_id}")
    List<BoardEntity> getBoardsById(Integer user_id);

    @Select("select * from app_board")
    List<BoardEntity> getBoardsByAll();

    @Update("update app_board set overall_score = #{overall_score}, " +
            "score_1 = #{score_1},score_2 = #{score_2},score_3 = #{score_3},score_4 = #{score_4},score_5 = #{score_5} WHERE board_id = #{board_id}")
    void updateBoardScore(Integer board_id, Float overall_score, Integer score_1, Integer score_2, Integer score_3, Integer score_4, Integer score_5);
}




