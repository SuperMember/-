package com.yxg.football.backendweb;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yxg.football.backendweb.dao.CircleSearchDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.entity.Circle;
import com.yxg.football.backendweb.service.UserService;
import com.yxg.football.backendweb.util.Const;
import com.yxg.football.backendweb.util.HttpUtil;
import com.yxg.football.backendweb.util.JsoupUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendWebApplicationTests {
    @Autowired
    UserService userService;

    @Autowired
    RedisDao redisDao;

    @Autowired
    HttpUtil httpUtil;

    @Autowired
    CircleSearchDao circleSearchDao;

    @Autowired
    Gson gson;

    @Autowired
    JsoupUtil jsoupUtil;

    @Test
    public void contextLoads() {
        System.out.println(redisDao.likeComment(2, 100));
    }

    @Test
    public void test() {
        //String html = httpUtil.getBody("http://www.dongqiudi.com/match/situation/50888460");
        //String html = httpUtil.getGameDetail("https://www.dongqiudi.com/team/429.html");
//        String html = httpUtil.getGameDetail("https://www.dongqiudi.com/player/50003056.html");
//        Map<String, Object> list = jsoupUtil.getPlayerInfo(html);
        String html = httpUtil.getBody("https://www.dongqiudi.com/player/429.html");
        System.out.println("dd");
    }

    @Test
    public void t() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://oqbwskol.api.lncld.net/1.1/requestSmsCode");
        httpPost.setHeader("X-LC-Id", Const.APPID);
        httpPost.setHeader("X-LC-Key", Const.APPKEY);
        httpPost.setHeader("Content-Type", "application/json");
        //List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        params.put("mobilePhoneNumber", phone);
        //nvps.add(new BasicNameValuePair("mobilePhoneNumber", "18219111622"));
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobilePhoneNumber", "18219111622");
        StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");//解决中文乱码问题
        try {
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String srtResult = "";
            if (statusCode != HttpStatus.SC_OK) {
                srtResult = EntityUtils.toString(response.getEntity());
                System.out.println(srtResult);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void elasticsearch() {
        Circle circle = new Circle();
        circle.setID(8);
        circle.setINTRODUCTION("热血一回");
        circle.setCOUNT(0);
        circle.setSTATUE(1);
        circle.setTitle("妖精的尾巴");
        circle.setTYPE(3);
        circle.setIMG("http://119.29.73.127/group1/M00/00/03/CodEe1rZltyAGoHHAAAoS-eYTck143.jpg");
        circle.setBACKGROUND("http://119.29.73.127/group1/M00/00/03/CodEe1rZlu2Ae-YoAAI_pglnu_M889.jpg");
        circle.setUSERID(6);
        circle.setCREATED(new Date());
        circleSearchDao.save(circle);
    }

}
