package com.yxg.football.backendweb.controller;


import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/football/api")
public class IndexController {

    @Autowired
    IndexService indexService;

    /*
    * 首页数据
    * */
    @GetMapping("/index")
    public ResponseEntity<?> index(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(defaultValue = "0") Integer more) throws Exception {
        List<Map<String, Object>> result = indexService.getIndexData(page, size);
        Map<String, Object> resultMap = new HashMap<>();
        if (more == 0) {
            resultMap.put("recommend", indexService.getIndexRecommendGame());
            resultMap.put("swiper", result.subList(0, 5));
        }
        resultMap.put("list", result);
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }


    /*
    * 比赛记录标题
    * */
    @GetMapping("/game/list")
    public ResponseEntity<?> getGameTitle() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            result = indexService.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 比赛记录
    * */
    @GetMapping("/game")
    public ResponseEntity<?> getGameList(String date, String tab) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = indexService.getGameSchedule(date, tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 比赛记录详情
    * */
    @GetMapping("/game/detail/{id}")
    public ResponseEntity<?> getGameDetail(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = indexService.getGameDetail(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 记录详情中的赛况
    * */
    @GetMapping("/game/detail/situation/{id}")
    public ResponseEntity<?> getGameSituation(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = indexService.getGameDetailSituation(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
   * 记录详情中的阵容
   * */
    @GetMapping("/game/detail/lineup/{id}")
    public ResponseEntity<?> getGameLineUp(@PathVariable String id) {
        Map<String, Object> result = null;
        try {
            result = indexService.getGameDetailLineUp(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
   * 记录详情中的赔率
   * */
    @GetMapping("/game/detail/odd/{id}")
    public ResponseEntity<?> getGameOdd(@PathVariable String id) {
        Map<String, Object> result = null;
        try {
            result = indexService.getGameDetailOdd(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
  * 记录详情中的集锦
  * */
    @GetMapping("/game/detail/highlights/{id}")
    public ResponseEntity<?> getGameHighlights(@PathVariable String id) {
        List<Map<String, Object>> result = null;
        try {
            result = indexService.getGameDetailHighlights(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 联赛信息
    * */
    @GetMapping("/league/data")
    public ResponseEntity<?> getLeague(String id,
                                       @RequestParam(defaultValue = "team_rank") String type,
                                       @RequestParam(required = false) String current,
                                       @RequestParam(required = false) String season_id,
                                       @RequestParam(required = false) String round_id) {
        Map<String, Object> result = null;
        try {
            result = indexService.getGameInfo(id, type, current, season_id, round_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 联赛
    * */
    @GetMapping("/league")
    public ResponseEntity<?> getLeague() {
        List<Map<String, Object>> result = null;
        try {
            result = indexService.getLeague();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 球员信息
    * */
    @GetMapping("/player")
    public ResponseEntity<?> getPlayerInfo(Integer id) {
        Map<String, Object> result = null;
        try {
            result = indexService.getPlayerInfo(id + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 球队信息
    * */
    @GetMapping("/team")
    public ResponseEntity<?> getTeamInfo(Integer id) {
        Map<String, Object> result = null;
        try {
            result = indexService.getTeamInfo(id + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 详情
    * */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDatail(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 视频集合
    * */
    @GetMapping("/video")
    public ResponseEntity<?> video(@RequestParam(defaultValue = "1") Integer page) {
        List<Map<String, Object>> resultList = null;
        try {
            resultList = indexService.getVideo(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(resultList), HttpStatus.OK);
    }

    /*
    * 某条文章的详情
    * */
    @GetMapping("/index/article")
    public ResponseEntity<?> getOneArticle(Integer id, Integer type) throws Exception {
        Map<String, Object> result = indexService.getArticle(id, type);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 比赛点赞
    * */
    @PostMapping("/index/game")
    public ResponseEntity<?> setZan(@RequestBody Map<String, Object> params) {
        indexService.setZan(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }
}
