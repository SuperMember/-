package com.yxg.football.backendweb.util;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Configuration;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class JsoupUtil {

    //解析html,获取有用的数据
    //首页数据
    public List<Map<String, Object>> parseHtml(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div .p_video");
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            Element element = elements.get(i);
            Element a = element.select(".img_outer a").get(0);
            map.put("url", a.attr("href"));
            map.put("title", a.attr("title"));
            map.put("img", a.select("img").attr("src"));
            Element time = element.select("dl dd").last();
            map.put("time", time.text());
            resultList.add(map);
        }
        return resultList;
    }

    //详情
    public String detailHtml(String html) {
        Document document = Jsoup.parse(html);
        Element element = document.select("div .voice-main").get(0);
        return element.html();
    }

    //比赛记录的标题
    public List<Map<String, Object>> getTitle(String html) {
        List<Map<String, Object>> result = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#match_list a");
        for (int i = 0; i < elements.size(); i++) {
            Element a = elements.get(i);
            Map<String, Object> map = new HashMap<>();
            String link = a.attr("href");
            map.put("tab", link.substring(link.lastIndexOf("=") + 1));
            map.put("name", a.text());
            result.add(map);
        }
        return result;
    }

    //赛事信息
    public List<Map<String, Object>> detailGame(String html) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".list tr");
        List<Map<String, Object>> gameList = null;
        Map<String, Object> titleMap = new HashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Element th = null;
            if (element.select("th") != null && element.select("th").size() != 0) {
                th = element.select("th").get(0);
            }
            Map<String, Object> map = new HashMap<>();
            if (th != null) {
                if (gameList == null) {
                    gameList = new ArrayList<>();
                } else {
                    titleMap.put("game", gameList);
                    resultList.add(titleMap);
                    gameList = new ArrayList<>();
                    titleMap = new HashMap<>();
                }
                titleMap.put("time", th.html());
                continue;
            }
            //时间
            String time = element.select(".times").get(0).text();
            map.put("time", time);
            //联赛
            String round = element.select(".round").get(0).text();
            map.put("round", round);
            //主队
            Map<String, Object> away = new HashMap<>();
            away.put("awayName", element.select(".away").get(0).text());
            String link = element.select(".away a").get(0).attr("href");
            away.put("awayLink", link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".")));
            away.put("awayImg", element.select(".away a img").get(0).attr("src"));
            map.put("away", away);
            //比分
            if (element.select(".stat") != null && element.select(".stat").size() != 0) {
                String stat = element.select(".stat").get(0).text();
                map.put("stat", stat.substring(stat.lastIndexOf("/") + 1));
            }
            //客队
            Map<String, Object> home = new HashMap<>();
            home.put("homeName", element.select(".home").get(0).text());
            String homelink = element.select(".home a").get(0).attr("href");
            home.put("homeLink", homelink.substring(homelink.lastIndexOf("/") + 1, homelink.lastIndexOf(".")));
            home.put("homeImg", element.select(".home a img").get(0).attr("src"));
            map.put("home", home);
            //比赛数据
            String temp = element.select(".times").get(0).attr("onclick");
            String tp = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
            String gameLink = tp.substring(tp.lastIndexOf("/") + 1);
            map.put("link", gameLink);
            gameList.add(map);
        }
        if (titleMap.get("game") == null && gameList != null && gameList.size() != 0) {
            //处理最后一项
            titleMap.put("game", gameList);
            resultList.add(titleMap);
        }
        return resultList;
    }

    //赛事信息详情
    public Map<String, Object> getGameDetail(String html) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements a = document.select("div.match_info a");
        //主队客队的信息
        for (int i = 0; i < a.size(); i++) {
            Element element = a.get(i);
            Map<String, Object> map = new HashMap<>();
            String href = element.attr("href");
            map.put("team", href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".")));
            map.put("img", element.select(".team_img").attr("src"));
            map.put("teamName", element.select(".team_name").text());
            result.add(map);
        }
        resultMap.put("teamInfo", result);
        //比分信息
        Elements stat = document.select(".match_info .stat div");
        List<String> info = new ArrayList<>();
        for (int i = 0; i < stat.size(); i++) {
            info.add(stat.get(i).html().replaceAll("&nbsp;", " ").replaceAll("&amp;nbsp", " "));
        }
        resultMap.put("stat", info);
        return resultMap;
    }

    //赛事详情中的赛况
    public Map<String, Object> getGameDetailSituation(String html) {
        Map<String, Object> resultMap = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#timeline li");
        if (elements != null && elements.size() != 0) {
            //事件
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (int i = 1; i < elements.size(); i++) {
                Element element = elements.get(i);
                Map<String, Object> map = new HashMap<>();
                map.put("time", element.select(".num").text());
                Elements divType = element.select(".home");
                if (divType != null && divType.size() != 0) {
                    map.put("position", "home");
                } else {
                    map.put("position", "away");
                }
                Elements p = element.select("div p a img");
                List<Map<String, Object>> person = new ArrayList<>();
                for (int j = 0; j < p.size(); j++) {
                    Map<String, Object> personMap = new HashMap<>();
                    personMap.put("img", p.get(j).attr("src"));
                    personMap.put("name", element.select("div p a .text_lim").get(j).text());
                    person.add(personMap);
                }
                map.put("person", person);
                resultList.add(map);
            }
            resultMap.put("event", resultList);
            //技术统计
            Elements es = document.select(".status table tr");
            List<Map<String, Object>> status = new ArrayList<>();
            for (int i = 0; i < es.size(); i++) {
                Element tr = es.get(i);
                Map<String, Object> statueMap = new HashMap<>();
                statueMap.put("away", tr.select(".away .num").get(0).text());
                statueMap.put("statue", tr.select(".name").get(0).text());
                statueMap.put("home", tr.select(".home .num").get(0).text());
                status.add(statueMap);
            }
            resultMap.put("statue", status);
        } else {
            resultMap.put("event", null);
            resultMap.put("statue", null);
        }
        return resultMap;
    }

    //赛事详情中的阵容
    public Map<String, Object> getGameDetailSquad(String html) {
        Map<String, Object> resultMap = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".match_stat .stat_list");
        if (elements != null && elements.size() != 0) {
            //正选
            List<Map<String, Object>> stat = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                Map<String, Object> map = new HashMap<>();
                Element element = elements.get(i);
                Elements tds = element.select("td");
                //主队
                String num = tds.get(0).text();
                map.put("away_num", num);
                String position = tds.get(1).text();
                map.put("away_position", position);
                Map<String, Object> infoMap = new HashMap<>();
                String link = tds.get(2).select("a").attr("href");
                infoMap.put("away_link", link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".")));
                infoMap.put("away_play_name", tds.get(2).select(".player_name").text());
                infoMap.put("away_img", tds.get(2).select("a img").attr("src"));
                map.put("away_info", infoMap);
                //客队
                infoMap = new HashMap<>();
                String homeLink = tds.get(3).select("a").attr("href");
                infoMap.put("home_link", homeLink.substring(homeLink.lastIndexOf("/") + 1, homeLink.lastIndexOf(".")));
                infoMap.put("home_play_name", tds.get(3).select(".player_name").text());
                infoMap.put("home_img", tds.get(3).select("a img").attr("src"));
                map.put("home_info", infoMap);
                map.put("home_position", tds.get(4).text());
                map.put("home_num", tds.get(5).text());
                stat.add(map);
            }
            resultMap.put("match_stat", stat);
            stat = new ArrayList<>();
            //替补
            Elements rank = document.select(".top_rank .match_list .stat_list");
            for (int i = 0; i < rank.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                Elements tds = rank.get(i).select("td");
                //主队
                map.put("away_rank_num", tds.get(0).text());
                map.put("away_rank_position", tds.get(1).text());
                Map<String, Object> infoMap = new HashMap<>();
                infoMap.put("away_rank_link", tds.get(2).select("a").attr("href"));
                infoMap.put("away_rank_play_name", tds.get(2).select("a").text());
                infoMap.put("away_rank_img", tds.get(2).select("a img").attr("src"));
                map.put("away_rank_info", infoMap);
                //客队
                infoMap = new HashMap<>();
                infoMap.put("home_rank_link", tds.get(3).select("a").attr("href"));
                infoMap.put("home_rank_play_name", tds.get(3).select("a").text());
                infoMap.put("home_rank_img", tds.get(3).select("a img").attr("src"));
                map.put("home_rank_info", infoMap);
                map.put("home_rank_position", tds.get(4).text());
                map.put("home_rank_num", tds.get(5).text());
                stat.add(map);
            }
            resultMap.put("match_toprank", stat);
        } else {
            resultMap.put("match_stat", null);
            resultMap.put("match_toprank", null);
        }
        return resultMap;
    }

    //赛事详情中的赔率
    public Map<String, Object> getGameDetailOdd(String html) {
        Map<String, Object> map = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("table.match_list");
        for (int i = 0; i < elements.size(); i++) {
            Elements trs = elements.get(i).select(".stat_list");
            List<Map<String, Object>> list = new ArrayList<>();
            for (int j = 0; j < trs.size(); j++) {
                Map<String, Object> trMap = new HashMap<>();
                Elements tds = trs.get(j).select("td");
                trMap.put("company", tds.get(0).text());
                trMap.put("type", tds.get(1).select("span").text());
                trMap.put("home", tds.get(2).select("span").text());
                trMap.put("ball", tds.get(3).select("span").text());
                trMap.put("away", tds.get(4).select("span").text());
                trMap.put("time", tds.get(5).text());
                list.add(trMap);
            }
            if (i == 0) {
                map.put("asia", list);
            } else if (i == 1) {
                map.put("ball", list);
            } else {
                map.put("european", list);
            }
        }
        return map;
    }

    //赛事详情中的集锦
    public List<Map<String, Object>> getGameDetailHighlights(String html) {
        List<Map<String, Object>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".gif_container li");
        for (int i = 0; i < elements.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("src", elements.get(i).select("a").attr("href"));
            map.put("text", elements.get(i).select(".text_con").text().trim());
            list.add(map);
        }
        return list;
    }


    //联赛信息(积分榜)
    public Map<String, Object> getLeagueScore(String html) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".list_1 tr");
        for (int i = 2; i < elements.size(); i++) {
            Elements tds = elements.get(i).select("td");
            Map<String, Object> map = new HashMap<>();
            map.put("rank", tds.get(0).text());
            Map<String, Object> team = new HashMap<>();
            String teamLink = tds.get(1).select("a").attr("href");
            team.put("teamLink", teamLink.substring(teamLink.lastIndexOf("/") + 1, teamLink.lastIndexOf(".")));
            team.put("teamImg", tds.get(1).select("a img").attr("src"));
            team.put("teamName", tds.get(1).select("a").text());
            map.put("team", team);
            map.put("num", tds.get(2).text());
            map.put("v", tds.get(3).text());
            map.put("h", tds.get(4).text());
            map.put("d", tds.get(5).text());
            map.put("goal", tds.get(6).text());
            map.put("lost", tds.get(7).text());
            map.put("gl", tds.get(8).text());
            map.put("score", tds.get(9).text());
            list.add(map);
        }
        resultMap.put("data", list);
        resultMap.put("top_rank", document.select(".list_1 .top_rank").size());
        resultMap.put("bottom_rank", document.select(".list_1 .bottom_rank").size());
        return resultMap;
    }

    //联赛信息(射手榜/助攻榜)
    public Map<String, Object> getLeagueGoal(String html) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".list_1 tr");
        for (int i = 1; i < elements.size(); i++) {
            Elements tds = elements.get(i).select("td");
            Map<String, Object> map = new HashMap<>();
            map.put("rank", tds.get(0).text());
            Map<String, Object> player = new HashMap<>();
            String playerLink = tds.get(1).select("a").attr("href");
            player.put("playerLink", playerLink.substring(playerLink.lastIndexOf("/") + 1, playerLink.lastIndexOf(".")));
            player.put("playerImg", tds.get(1).select("a img").attr("src"));
            player.put("playerName", tds.get(1).select("a").text());
            map.put("player", player);
            Map<String, Object> team = new HashMap<>();
            String teamLink = tds.get(2).select("a").attr("href");
            team.put("teamLink", teamLink.substring(teamLink.lastIndexOf("/") + 1, teamLink.lastIndexOf(".")));
            team.put("teamImg", tds.get(2).select("a img").attr("src"));
            team.put("teamName", tds.get(2).select("a").text());
            map.put("team", team);
            map.put("stat", tds.get(3).text());
            list.add(map);
        }
        result.put("data", list);
        return result;
    }

    //联赛信息(赛程)
    public Map<String, Object> getLeagueTime(String html) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".matchinfo");
        for (int i = 0; i < elements.size(); i++) {
            Map<String, Object> tdMap = new HashMap<>();
            Elements tds = elements.get(i).select("td");
            tdMap.put("time", tds.get(0).attr("utc"));
            Map<String, Object> away = new HashMap<>();
            String awayLink = tds.get(1).select("a").attr("href");
            away.put("awayLink", awayLink.substring(awayLink.lastIndexOf("/") + 1, awayLink.lastIndexOf(".")));
            away.put("awayImg", tds.get(1).select("a img").attr("src"));
            away.put("awayName", tds.get(1).select("a").text());
            tdMap.put("away", away);
            tdMap.put("status", tds.get(2).text());
            Map<String, Object> home = new HashMap<>();
            String homeLink = tds.get(3).select("a").attr("href");
            home.put("homeLink", homeLink.substring(homeLink.lastIndexOf("/") + 1, homeLink.lastIndexOf(".")));
            home.put("homeImg", tds.get(3).select("a img").attr("src"));
            home.put("homeName", tds.get(3).select("a").text());
            tdMap.put("home", home);
            list.add(tdMap);
        }
        map.put("data", list);
        map.put("current", document.select(".list_2 thead tr #schedule_title").text());
        String url = document.select(".list_2 thead tr .prev a").attr("href");
        if (url != null && !StringUtil.isBlank(url)) {
            map.put("season_id", url.substring(url.indexOf("season_id=") + 10, url.indexOf("&round_id=")));
            map.put("round_id", url.substring(url.indexOf("round_id=") + 9, url.indexOf("&gameweek=")));
        }
        return map;
    }

    //联赛
    public List<Map<String, Object>> getLeague(String html) {
        List<Map<String, Object>> list = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#stat_list a");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("name", element.text());
            String link = element.attr("href");
            map.put("link", link.substring(link.indexOf("=") + 1));
            list.add(map);
        }
        return list;
    }

    //球队详细信息
    public Map<String, Object> getTeamInfo(String html) {
        Map<String, Object> resultMap = new HashMap<>();
        Document document = Jsoup.parse(html);
        //球队信息
        Map<String, Object> teamMap = new HashMap<>();
        if (document.select(".team_info") != null) {
            teamMap.put("teamImg", document.select(".team_info .img_wrapper img").attr("src"));
            teamMap.put("teamName", document.select(".base_info .name").text());
            teamMap.put("teamCInt", document.select(".base_info img").attr("src"));
            teamMap.put("enName", document.select(".base_info en_name").text());
            Elements elements = document.select(".detail_info li");
            List<String> infoList = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                infoList.add(elements.get(i).select("span").text());
            }
            teamMap.put("detailInfo", infoList);
            resultMap.put("team", teamMap);
        }
        //赛程
        if (document.select(".schedule") != null) {
            Elements elements = document.select(".schedule_list tbody");
            List<List<Map<String, Object>>> resultList = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if (element.attr("style", "display:none;") == null) {
                    resultMap.put("currentIndex", i);
                }
                Elements trs = element.select(".stat_list");
                List<Map<String, Object>> list = new ArrayList<>();
                for (int j = 0; j < trs.size(); j++) {
                    Elements tds = trs.get(j).select("td");
                    Map<String, Object> map = new HashMap<>();
                    map.put("hidden", tds.get(0).text());
                    map.put("time", tds.get(1).text());
                    map.put("gameweek", tds.get(2).text());
                    Map<String, Object> away = new HashMap<>();
                    String href = tds.get(3).select("a").attr("href");
                    away.put("awayLink", href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".")));
                    away.put("awayImg", tds.get(3).select("a img").attr("src"));
                    away.put("awayName", tds.get(3).select("a").text());
                    map.put("away", away);
                    String stat = tds.get(4).select("span").text();
                    //判断结果
                    String rstat[] = stat.split(" ");
                    if (rstat != null && rstat.length == 2) {
                        if (Integer.parseInt(rstat[0]) > Integer.parseInt(rstat[1])) {
                            map.put("result", 0);//主胜
                        } else if (Integer.parseInt(rstat[0]) < Integer.parseInt(rstat[1])) {
                            //客胜
                            map.put("result", 1);
                        } else {
                            //平局
                            map.put("result", 2);
                        }
                    }
                    map.put("status", stat.replace(" ", ":"));
                    Map<String, Object> home = new HashMap<>();
                    String homeHref = tds.get(5).select("a").attr("href");
                    home.put("homeLink", homeHref.substring(homeHref.lastIndexOf("/") + 1, homeHref.lastIndexOf(".")));
                    home.put("homeImg", tds.get(5).select("a img").attr("src"));
                    home.put("homeName", tds.get(5).select("a").text());
                    map.put("home", home);
                    list.add(map);
                }
                resultList.add(list);
            }
            resultMap.put("schedule", resultList);
        }
        //球队成员
        if (document.select(".team_stat") != null) {
            //TODO
            Elements trs = document.select(".team_stat .stat_list");
            List<Map<String, Object>> memberList = new ArrayList<>();
            for (int i = 0; i < trs.size(); i++) {
                //位置
                Map<String, Object> member = new HashMap<>();
                Elements tds = trs.get(i).select("td");
                member.put("position", tds.get(0).text());
                member.put("num", tds.get(1).text());
                Map<String, Object> player = new HashMap<>();
                player.put("playerImg", tds.get(2).select("img").attr("src"));
                if (tds.get(2).select("a") != null && tds.get(2).select("a").size() != 0) {
                    String link = tds.get(2).select("a").attr("href");
                    player.put("playerLink", link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".")));
                }
                player.put("playerName", tds.get(2).select("span").text());
                member.put("player", player);
                member.put("show", tds.get(3).text());
                member.put("goal", tds.get(4).text());
                member.put("int", tds.get(5).select("img").attr("src"));
                memberList.add(member);
            }
//            resultMap.put("member", memberList);

            //整合
            //教练
            List<Map<String, Object>> coach = new ArrayList<>();
            //前锋
            List<Map<String, Object>> striker = new ArrayList<>();
            //中场
            List<Map<String, Object>> midfield = new ArrayList<>();
            //后卫
            List<Map<String, Object>> defender = new ArrayList<>();
            //守门员
            List<Map<String, Object>> goalkeeper = new ArrayList<>();
            for (int i = 0; i < memberList.size(); i++) {
                Map<String, Object> m = memberList.get(i);
                if (m.get("position").equals("教练") || m.get("position").equals("助理教练")) {
                    coach.add(m);
                } else if (m.get("position").equals("前锋")) {
                    striker.add(m);
                } else if (m.get("position").equals("中场")) {
                    midfield.add(m);
                } else if (m.get("position").equals("后卫")) {
                    defender.add(m);
                } else {
                    //守门员
                    goalkeeper.add(m);
                }
            }
            Map<String, Object> rm = new HashMap<>();
            rm.put("coach", coach);
            rm.put("striker", striker);
            rm.put("midfield", midfield);
            rm.put("defender", defender);
            rm.put("goalkeeper", goalkeeper);
            resultMap.put("member", rm);
        }
        //荣誉记录
        if (document.select(".honour_record") != null) {
            //TODO
            Elements elements = document.select(".honour_item");
            List<Map<String, Object>> honourList = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                Map<String, Object> honour = new HashMap<>();
                Element element = elements.get(i);
                honour.put("honourImg", element.select(".honour_item_title img").attr("src"));
                honour.put("honourName", element.select(".honour_item_title span").text().trim().replace(" ", "×"));
                honour.put("honourTime", element.select(".session_area span").text());
                honourList.add(honour);
            }
            resultMap.put("honour", honourList);
        }
        return resultMap;
    }

    //球员详细信息
    public Map<String, Object> getPlayerInfo(String html) {
        Map<String, Object> map = new HashMap<>();
        Document document = Jsoup.parse(html);
        //个人信息
        if (document.select(".player_info") != null) {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("playerName", document.select(".base_info h1").text());
            Elements imgs = document.select(".base_info img");
            List<String> imgList = new ArrayList<>();
            for (int i = 0; i < imgs.size(); i++) {
                imgList.add(imgs.get(i).attr("src"));
            }
            playerMap.put("playerIntImg", imgList);
            playerMap.put("playerEnName", document.select(".base_info .en_name").text());
            Elements lis = document.select(".detail_info li");
            List<String> detailList = new ArrayList<>();
            for (int i = 0; i < lis.size(); i++) {
                detailList.add(lis.get(i).select("span").text());
            }
            playerMap.put("playerDetail", detailList);
            playerMap.put("playerImg", document.select(".player_img").attr("src"));
            map.put("player", playerMap);
        }
        //比赛数据
        if (document.select(".match_stat") != null) {
            Elements elements = document.select(".match_list");
            List<List<List<String>>> result = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                List<List<String>> resultList = new ArrayList<>();
                Elements trs = elements.get(i).select(".stat_list");
                List<String> tdList = new ArrayList<>();
                for (int j = 0; j < trs.size(); j++) {
                    Elements tds = trs.get(j).select("td");
                    tdList = new ArrayList<>();
                    for (int k = 0; k < tds.size(); k++) {
                        Element td = tds.get(k);
                        if (k == 1) {
                            String href = td.select("a").attr("href");
                            tdList.add(href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".")));
                        }
                        tdList.add(td.text());
                    }
                    resultList.add(tdList);
                }
                result.add(resultList);
            }
            map.put("match_stat", result);
        }
        //荣誉
        if (document.select(".honour_record") != null) {
            Elements es = document.select(".honour_item");
            List<Map<String, Object>> honourList = new ArrayList<>();
            for (int i = 0; i < es.size(); i++) {
                Map<String, Object> honour = new HashMap<>();
                Element element = es.get(i);
                honour.put("honourImg", element.select(".honour_item_title img").attr("src"));
                honour.put("honourName", element.select(".honour_item_title span").text());
                honour.put("honourTime", element.select(".session_area span").text());
                honourList.add(honour);
            }
            map.put("honour", honourList);
        }
        //转会
        if (document.select(".transfer") != null) {
            Elements elements = document.select(".transfer_list .transfer_item");
            List<Map<String, Object>> transferList = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                Map<String, Object> transferMap = new HashMap<>();
                transferMap.put("time", element.select(".transfer_time").text());
                Elements as = element.select("a");
                List<Map<String, Object>> detailList = new ArrayList<>();
                for (int j = 0; j < as.size(); j++) {
                    Map<String, Object> detailMap = new HashMap<>();
                    String href = as.get(j).attr("href");
                    detailMap.put("transferLink", href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".")));
                    detailMap.put("transferImg", as.get(j).select(".team_logo").attr("src"));
                    detailMap.put("transferName", as.get(j).select("span").text());
                    detailList.add(detailMap);
                }
                transferMap.put("transferItems", detailList);
                transferMap.put("transferDetail", element.select(".transfer_detail").text());
                transferList.add(transferMap);
            }
            map.put("transfer", transferList);
        }
        //伤病
        if (document.select(".invalid") != null) {
            List<Map<String, Object>> invalidList = new ArrayList<>();
            Elements elements = document.select(".invalid_list .invalid_item");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                Map<String, Object> invalidMap = new HashMap<>();
                invalidMap.put("team", element.select("span").get(0).text());
                invalidMap.put("detail", element.select("span").get(1).text());
                invalidMap.put("time", element.select("span").get(2).text());
                invalidList.add(invalidMap);
            }
            map.put("invalid", invalidList);
        }
        //综合能力
        if (document.select(".rightmain .ability") != null) {
            Map<String, Object> abilityMap = new HashMap<>();
            Element element = document.select(".rightmain .ability").get(0);
            abilityMap.put("title", element.select("#title").text());
            Elements boxes = element.select(".box_chart .item");
            List<String> abilityList = new ArrayList<>();
            for (int i = 0; i < boxes.size(); i++) {
                Element div = boxes.get(i);
                abilityList.add(div.text());
            }
            abilityMap.put("ability", abilityList);
            Elements des = element.select(".des .list");
            List<Map<String, Object>> desList = new ArrayList<>();
            for (int i = 0; i < des.size(); i++) {
                Element div = des.get(i);
                Map<String, Object> desMap = new HashMap<>();
                desMap.put("name", div.select("span").text());
                if (i == 0) {
                    desMap.put("des", div.select("img").attr("src"));
                } else {
                    desMap.put("des", div.select(".ranks .rank_f").size());
                }
                desList.add(desMap);
            }
            abilityMap.put("des", desList);
            map.put("ability", abilityMap);
        }
        return map;
    }

    public List<Map<String, Object>> getIndexGameRecommended(String html) {
        List<Map<String, Object>> result = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#list li");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Map<String, Object> resultMap = new HashMap<>();
            String gameLink = element.select("a").attr("href");
            resultMap.put("gameLink", gameLink.substring(gameLink.lastIndexOf("/") + 1).trim());
            Map<String, Object> awayMap = new HashMap<>();
            awayMap.put("awayImg", element.select(".away img").attr("src"));
            awayMap.put("awayName", element.select(".away").text());
            resultMap.put("away", awayMap);
            resultMap.put("gameName", element.select(".stat h2").text());
            resultMap.put("gameStat", element.select(".stat h3").text());
            Elements p = element.select(".stat p i");
            if (p != null && p.size() != 0) {
                resultMap.put("gameTime", p.get(0).text());
            }
            Map<String, Object> homeMap = new HashMap<>();
            homeMap.put("homeImg", element.select(".home img").attr("src"));
            homeMap.put("homeName", element.select(".home").text());
            resultMap.put("home", homeMap);
            result.add(resultMap);
        }
        return result;
    }

}
