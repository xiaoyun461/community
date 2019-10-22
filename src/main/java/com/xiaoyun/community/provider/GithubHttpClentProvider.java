package com.xiaoyun.community.provider;

import com.alibaba.fastjson.JSON;
import com.xiaoyun.community.dto.AccessTokenDTO;
import com.xiaoyun.community.dto.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class GithubHttpClentProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
        StringBuffer params = new StringBuffer();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            params.append("client_id=" + accessTokenDTO.getCliend_id());
            params.append("&");
            params.append("client_secret=" + accessTokenDTO.getClient_secret());
            params.append("&");
            params.append("code=" + accessTokenDTO.getCode());
            params.append("&");
            params.append("redirect_uri=" + accessTokenDTO.getRedirect_uri());
            params.append("&");
            params.append("state=" + accessTokenDTO.getState());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // 创建Post请求
        HttpPost httpPost = new HttpPost("https://github.com/login/oauth/access_token" + "?" + params);

        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        String token = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            log.info("响应状态为:{}" + response.getStatusLine());
            if (responseEntity != null) {
                log.info("响应内容长度为:{}" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                token = EntityUtils.toString(responseEntity).split("&")[0].split("=")[1];
            }


        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    public GithubUser getUser(String accessToken) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();


        // 创建Post请求
        HttpGet httpPost = new HttpGet("https://api.github.com/user?access_token=" + accessToken);

        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        GithubUser githubUser = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                log.info("响应内容长度为:{}" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                String string = EntityUtils.toString(responseEntity);
                System.out.println(string);
                githubUser = JSON.parseObject(string, GithubUser.class);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return githubUser;
    }
}
