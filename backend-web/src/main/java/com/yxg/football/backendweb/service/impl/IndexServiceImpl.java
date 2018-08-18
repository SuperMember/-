package com.yxg.football.backendweb.service.impl;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yxg.football.backendweb.dao.CircleDao;
import com.yxg.football.backendweb.dao.IndexDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.service.GameService;
import com.yxg.football.backendweb.service.IndexService;
import com.yxg.football.backendweb.util.Const;
import com.yxg.football.backendweb.util.HttpUtil;
import com.yxg.football.backendweb.util.JsoupUtil;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    Gson gson;
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    JsoupUtil jsoupUtil;
    @Autowired
    GameService gameService;
    @Autowired
    IndexDao indexDao;
    @Autowired
    RedisDao redisDao;

    /*
    * 比赛记录
    * */
    @Value("${index.game}")
    String gameUrl;
    /*
    * 比赛记录详情
    * */
    @Value("${index.game_detail}")
    String gameDetailUrl;
    /*
    * 阵容
    * */
    @Value("${index.game_squad}")
    String gameDetailSquad;

    /*
    * 赔率
    * */
    @Value("${index.game_odd}")
    String gameDetailOdd;

    /*
    * 集锦
    * */
    @Value("${index.game_highlights}")
    String gameDetailHighlights;

    /*
    * 首页
    * */
    @Value("${index.url}")
    String gameIndex;


    @Override
    public List<Map<String, Object>> getIndexData(Integer page, Integer size) throws Exception {
        List<Map<String, Object>> list = indexDao.getIndexData(page, size);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> resultMap = list.get(i);
            //设置评论数
            Integer count = redisDao.getArticleCommentCount((Integer) resultMap.get("ID"), (Integer) resultMap.get("TYPE") == 0 ? 2 : (Integer) resultMap.get("TYPE"));
            resultMap.put("COUNT", count);
        }
        return list;
    }

    /*
        *
        *
        * */
    @Override
    public List<Map<String, Object>> getVideo(Integer page) throws Exception {
        return null;
    }


    /*
    * 比赛赛程(dqd)
    * */
    @Override
    public Map<String, Object> getGameSchedule(String date, String tab) {
        Map<String, Object> result = new HashMap<>();
        String url = gameUrl + "&date=" + date + "&tab=" + tab;
        String html = httpUtil.getBody(url);
        Map<String, Object> map = gson.fromJson(html, new TypeToken<Map<String, Object>>() {
        }.getType());
        List<Map<String, Object>> resultList = jsoupUtil.detailGame(((String) map.get("html")).replaceAll("\n", ""));
        result.put("next_date", map.get("next_date"));
        result.put("data", resultList);
        return result;
    }

    /*
    * 比赛赛程的详情(dqd)(加载头部)
    * */
    @Override
    public Map<String, Object> getGameDetail(String id) {
        String html = httpUtil.getBody(gameDetailUrl + id);
        Map<String, Object> result = jsoupUtil.getGameDetail(html);
        result.put("zan", redisDao.getZan(id));
        return result;
    }

    /*
    * 比赛赛程详情中的赛况(dqd)
    * */
    @Override
    public Map<String, Object> getGameDetailSituation(String id) {
        String html = httpUtil.getBody(gameDetailUrl + id);
        return jsoupUtil.getGameDetailSituation(html);
    }

    /*
    * 比赛赛程详情中的阵容(dqd)
    * */
    @Override
    public Map<String, Object> getGameDetailLineUp(String id) {
        String html = httpUtil.getBody(gameDetailSquad + id);
        return jsoupUtil.getGameDetailSquad(html);
    }

    /*
    * 比赛赛程详情中的赔率(dqd)
    * */
    @Override
    public Map<String, Object> getGameDetailOdd(String id) {
        String html = httpUtil.getBody(gameDetailOdd + id);
        return jsoupUtil.getGameDetailOdd(html);
    }

    /*
    * 比赛赛程详情中的集锦(dqd)
    * */
    @Override
    public List<Map<String, Object>> getGameDetailHighlights(String id) {
        String html = httpUtil.getBody(gameDetailHighlights + id);
        return jsoupUtil.getGameDetailHighlights(html);
    }

    /*
    * 联赛信息
    * */
    @Override
    public Map<String, Object> getGameInfo(String id, String type, String current, String season_id, String round_id) {
        String html = httpUtil.getBody(Const.LEAGUE_URL + "?competition=" + id + "&type=" + type);
        if (type.equals("team_rank")) {
            //积分榜
            return jsoupUtil.getLeagueScore(html);
        } else if (type.equals("goal_rank")) {
            //进球榜
            return jsoupUtil.getLeagueGoal(html);
        } else if (type.equals("assist_rank")) {
            //助攻榜
            return jsoupUtil.getLeagueGoal(html);
        } else {
            //赛程
            if (current != null && !StringUtil.isBlank(current)) {
                html = httpUtil.getBody(Const.LEAGUE_URL + "?competition=" + id + "&type=" + type + "&season_id=" + season_id + "&round_id=" + round_id + "&gameweek=" + current);
            }
            Map<String, Object> result = jsoupUtil.getLeagueTime(html);
            result.put("count", gameService.getGameCount(Integer.parseInt(id)));
            return result;
        }
    }


    //联赛
    @Override
    public List<Map<String, Object>> getLeague() {
        String html = httpUtil.getBody(Const.LEAGUE_URL);
        return jsoupUtil.getLeague(html);
    }

    //球队信息
    @Override
    public Map<String, Object> getTeamInfo(String id) {
        String html = httpUtil.getBody(Const.TEAM_INFO + id + ".html");
        return jsoupUtil.getTeamInfo(html);
    }

    //球员信息
    @Override
    public Map<String, Object> getPlayerInfo(String id) {
        String html = httpUtil.getBody(Const.PLAYER_INFO + id + ".html");
        return jsoupUtil.getPlayerInfo(html);
    }

    //比赛记录的标题
    @Override
    public List<Map<String, Object>> getTitle() {
        String html = httpUtil.getBody(Const.MATCH_URL);
        return jsoupUtil.getTitle(html);
    }


    /*
    * 获取某条文章的详细信息
    * */
    @Override
    public Map<String, Object> getArticle(Integer id, Integer type) {
        //如果是视频则设置播放数量
        Map<String, Object> resultMap = indexDao.getArticle(id, type);
        if (type == 1) {
            //视频
            resultMap.put("playCount", redisDao.getVideoCount(id));
        }
        return resultMap;
    }


    /*
    * 赛事推荐
    * */
    @Override
    public List<Map<String, Object>> getIndexRecommendGame() {
        String html = httpUtil.getGameDetail(gameIndex);
        return jsoupUtil.getIndexGameRecommended(html);
    }

    /*
    * 设置赞
    * */
    @Override
    public void setZan(Map<String,Object> params) {
        String gameId= (String) params.get("gameId");
        String size= (String) params.get("size");
        redisDao.setZan(gameId, size);
    }


}
