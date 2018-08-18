package com.yxg.football.backendweb.dao;

import org.apache.xpath.operations.Bool;

import java.util.List;
import java.util.Map;

public interface CircleDao {

    /*
   * 根据圈子类型获取对应的帖子
   * */
    List<Map<String, Object>> getArticles(Integer cid, Integer page, Integer size);

    /*
    * 根据最新回复时间获取帖子
    * */
    List<Map<String, Object>> getArticlesByTime(Integer cid, Integer page, Integer size);

    /*
    * 根据时间获取最新的帖子
    * */
    List<Map<String, Object>> getAsByTime(Integer page, Integer size);


    /*
    * 提交帖子
    * */
    Integer insertArticle(Map<String, Object> params);

    /*
    * 删除帖子(用户自己删除或者遭到举报管理员删除)
    * */
    Integer deleteArticle(Integer aId);

    /*
    * 根据名称检测是否存在相同的圈子
    * */
    Boolean isExistCircle(String name);


    /*
    * 获取圈子
    * */
    List<Map<String, Object>> getCircles(Integer statue, Integer type);

    /*
    * 创建圈子
    * */
    Integer createCircle(Map<String, Object> params);

    /*
    * 删除圈子
    * */
    Integer deleteCircle(Integer id);

    /*
    * 修改圈子状态
    * */
    Integer updateCircle(String title);

    /*
    * 根据ID获取帖子内容
    * */
    Map<String, Object> getArticleById(Integer id);

    /*
    * 获取用户的帖子
    * */
    List<Map<String, Object>> getArticleByUserId(Integer userId);

    /*
    * 关注圈子
    * */
    Integer focusCircle(Integer userId, Integer circleId);


    /*
    * 获取用户关注的圈子
    * */
    List<Map<String, Object>> getCircleFocusByUserId(Integer userId);

    /*
    * 取消关注
    * */
    Integer unFocusCircle(Integer userId, Integer circleId);


    /*
    * 获取置顶帖
    * */
    List<Map<String, Object>> getTopList(Integer circleId);


    /*
    * 修改帖子的时间(评论)
    * */
    Integer updateTime(Integer id);

    /*
    * 修改帖子的时间(回复)
    * */
    Integer updateReplyTime(Integer commentId);

    /*
    * 获取圈主
    * */
    List<Map<String, Object>> getCircleOwner(Integer cId);
}
