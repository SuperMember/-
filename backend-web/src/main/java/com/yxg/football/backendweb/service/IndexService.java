package com.yxg.football.backendweb.service;

import java.util.List;
import java.util.Map;

public interface IndexService {
    List<Map<String, Object>> getIndexData(Integer page, Integer size) throws Exception;

    List<Map<String, Object>> getVideo(Integer page) throws Exception;

    Map<String, Object> getGameSchedule(String date, String tab);

    Map<String, Object> getGameDetail(String id);

    Map<String, Object> getGameDetailSituation(String html);

    Map<String, Object> getGameDetailLineUp(String html);

    Map<String, Object> getGameDetailOdd(String html);

    List<Map<String, Object>> getGameDetailHighlights(String html);

    Map<String, Object> getGameInfo(String id, String type, String current, String season_id, String round_id);

    List<Map<String, Object>> getLeague();

    Map<String, Object> getTeamInfo(String id);

    Map<String, Object> getPlayerInfo(String id);

    List<Map<String, Object>> getTitle();

    Map<String, Object> getArticle(Integer id, Integer type);

    /*
    * 赛事推荐
    * */
    List<Map<String, Object>> getIndexRecommendGame();

    /*
    * 设置赞
    * */
    void setZan(Map<String, Object> params);

}
