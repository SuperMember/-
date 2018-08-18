package com.yxg.football.backendweb.service;

import java.util.List;
import java.util.Map;

public interface CircleService {
    /*
    * 根据圈子类型获取对应的帖子
    * */
    List<Map<String, Object>> getArticles(Integer type, Integer cid, Integer page, Integer size);

    /*
    * 根据时间获取最新的帖子
    * */
    List<Map<String, Object>> getAsByTime(Integer page, Integer size);

    /*
    * 提交帖子
    * */
    Integer insertArticle(Map<String, Object> params);

    /*
    * 创建圈子
    * */
    Boolean createdCircle(Map<String, Object> params) throws Exception;

    /*
    * 删除帖子
    * */
    Integer deleteArticle(Integer aId);

    /*
  * 根据状态获取圈子(包括已经创建成功的圈子和正在创建的圈子)
  * */
    List<Map<String, Object>> getAllCircle(Integer statue, Integer type);

    /*
   * 支持创建圈子
   * */
    Boolean supportCircle(String title);


    /*
    * 根据id获取文章
    * */
    Map<String, Object> getArticleById(Integer id);

    /*
    * 根据用户id获取文章
    * */
    List<Map<String, Object>> getArticleByUserId(Integer userId);

    /*
    * 关注圈子
    * */
    Boolean focusCircle(Map<String, Object> params);

    /*
    * 判断用户是否关注过圈子
    * */
    Boolean isFocusCircle(Integer userId, Integer circleId);

    /*
    * 获取用户关注的圈子
    * */
    List<Map<String, Object>> getCircleFocusByUserId(Integer userId);

    /*
    * 设置视频播放数
    * */
    void setVideoCount(Integer articleId);

    /*
    * 获取视频播放数
    * */
    Integer getVideoCount(Integer articleId);

    /*
    * 获取置顶帖
    * */
    List<Map<String, Object>> getTopList(Integer circleId);

    /*
    * 用户活跃度排行榜
    * */
    void setRankByUserId(Integer userId, Integer point, Integer circleId);

    /*
    * 获取排行榜
    * */
    List<Map<String, Object>> getRank(Integer circleId, Integer page, Integer size);

    /*
    * 获取圈主
    * */
    List<Map<String, Object>> getCircleOwner(Integer cId);
}

