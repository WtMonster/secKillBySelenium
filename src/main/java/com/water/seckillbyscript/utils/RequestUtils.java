package com.water.seckillbyscript.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

/**
 * @author WtMonster
 * @date 2022/12/22 15:28
 */
public class RequestUtils {
    static CloseableHttpClient httpClient;
    static HttpGet getTimeStampReq;
    static {
        httpClient = HttpClients.createDefault();
        getTimeStampReq = new HttpGet("http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp");
        getTimeStampReq.setHeader("Connection", "keep-alive");
    }

    public static long getTaoBaoTimeStamp() {
        String res = null;
        try {
            long start = System.currentTimeMillis();
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(getTimeStampReq);
            System.out.println("请求耗时:" + (System.currentTimeMillis() - start));
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            res = EntityUtils.toString(httpEntity);//响应内容
            InputStream content = httpEntity.getContent();
            if (content != null){
                content.close();
            }
        } catch (Exception e) {
            System.out.println("获取时间戳失败");
        }
        JSONObject jsonObject = JSON.parseObject(res);
        String timeStamp = (String) jsonObject.getJSONObject("data").get("t");
        System.out.println("本地时差:" + (Long.valueOf(timeStamp) - System.currentTimeMillis()));
        return Long.valueOf(timeStamp);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            getTaoBaoTimeStamp();
        }
    }
}
