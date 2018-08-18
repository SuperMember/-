package com.yxg.football.backendweb.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class HttpUtil {
    @Autowired
    WebClient webClient;

    public String getBody(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)   //设置连接超时时间
                .setConnectionRequestTimeout(10000) // 设置请求超时时间
                .setSocketTimeout(10000)
                .build();

        HttpGet httpGet2 = new HttpGet(url);
        httpGet2.setConfig(requestConfig);
        httpGet2.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        //httpGet2.setHeader("Host", "m.dongqiudi.com/");
        httpGet2.setHeader("Content-Type", "text/html; charset=UTF-8");
        //httpGet2.setHeader("Server", "dqd-server/1.11.2.5");
        //httpGet2.setHeader("transfer-encoding", "chunked");
        //httpGet2.setHeader("vary", "Accept-Encoding");
        //httpGet2.setHeader("content-encoding", "gzip");
        //httpGet2.getParams().setParameter("http.protocol.single-cookie-header", true);
        //httpGet2.setHeader("x-cache-cfc", "MISS - 1517367681.245 -");
        //httpGet2.setHeader("Cookie", "dqduid=ChN02VpwE2mBOC/eAza8Ag==; laravel_session=eyJpdiI6Ik82THdLdG1MU2dodytyZ0JWazY1eE9CdHNNcGlxZVRHNUJDU1dWdW1HVlE9IiwidmFsdWUiOiJTSzRNR3NiT2F6V1JyNmJRUnIxdTdrcXNHZUVFQlFDTUdhVFwvbzZZd281VDdBejFjdCtRSmV4TTdjYWlhaU9ZeHAwQkZCYXl4RlIrQVRaaXd5bUpUTWc9PSIsIm1hYyI6IjkwZTU4NWFjMjA4OTkwNzMyZjIyMDg3NWE3OGQyMzcyNGZjNWFhMzkxODEzZDU2MTdjZTNjMDRhM2Q0YjczNWMifQ%3D%3D; Hm_lvt_662abe3e1ab2558f09503989c9076934=1517294445,1517365804; Hm_lpvt_662abe3e1ab2558f09503989c9076934=1517365804; Hm_lvt_ac3d87d81953324fa2119a12756e54bc=1517365589; Hm_lpvt_ac3d87d81953324fa2119a12756e54bc=1517365821; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2216145cbe300122-0c1579e3cf0c49-6b1b1079-1049088-16145cbe301210%22%2C%22%24device_id%22%3A%2216145cbe300122-0c1579e3cf0c49-6b1b1079-1049088-16145cbe301210%22%2C%22props%22%3A%7B%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%7D%7D");
        //httpGet2.setHeader("Cookie", "dqduid=ChN02VpwE2mBOC/eAza8Ag==; laravel_session=eyJpdiI6Ik82THdLdG1MU2dodytyZ0JWazY1eE9CdHNNcGlxZVRHNUJDU1dWdW1HVlE9IiwidmFsdWUiOiJTSzRNR3NiT2F6V1JyNmJRUnIxdTdrcXNHZUVFQlFDTUdhVFwvbzZZd281VDdBejFjdCtRSmV4TTdjYWlhaU9ZeHAwQkZCYXl4RlIrQVRaaXd5bUpUTWc9PSIsIm1hYyI6IjkwZTU4NWFjMjA4OTkwNzMyZjIyMDg3NWE3OGQyMzcyNGZjNWFhMzkxODEzZDU2MTdjZTNjMDRhM2Q0YjczNWMifQ%3D%3D; Hm_lvt_662abe3e1ab2558f09503989c9076934=1517294445,1517365804; Hm_lpvt_662abe3e1ab2558f09503989c9076934=1517365804; Hm_lvt_ac3d87d81953324fa2119a12756e54bc=1517365589; Hm_lpvt_ac3d87d81953324fa2119a12756e54bc=1517365821; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2216145cbe300122-0c1579e3cf0c49-6b1b1079-1049088-16145cbe301210%22%2C%22%24device_id%22%3A%2216145cbe300122-0c1579e3cf0c49-6b1b1079-1049088-16145cbe301210%22%2C%22props%22%3A%7B%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%7D%7D");
        //httpGet2.getParams().setParameter("http.protocol.allow-circular-redirects", true);
        String srtResult = "";
        try {
            HttpResponse httpResponse = client.execute(httpGet2);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                srtResult = EntityUtils.toString(httpResponse.getEntity(), "utf-8");//获得返回的结果
                return srtResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * 获取比赛数据
    * */
    public String getGameDetail(String html) {
        try {
            HtmlPage htmlPage = webClient.getPage(html);
            return htmlPage.asXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
