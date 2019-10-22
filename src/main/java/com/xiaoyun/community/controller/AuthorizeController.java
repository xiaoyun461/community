package com.xiaoyun.community.controller;

import com.xiaoyun.community.dto.AccessTokenDTO;
import com.xiaoyun.community.dto.GithubUser;
import com.xiaoyun.community.provider.GithubHttpClentProvider;
import com.xiaoyun.community.provider.GithubProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GithubHttpClentProvider githubHttpClentProvider;

    @Autowired
    private GithubProvider githubProvider;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setCliend_id("fdabf3c6e3f186d0a1c1");
        accessTokenDTO.setClient_secret("d334d05b50949aa0d487dc064545e01f0f96b1bd");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.setState(state);

        String accessToken = githubHttpClentProvider.getAccessToken(accessTokenDTO);
        log.info(accessToken);
        GithubUser user = githubHttpClentProvider.getUser(accessToken);
        log.info(user.toString());
        return "index";
    }
}
