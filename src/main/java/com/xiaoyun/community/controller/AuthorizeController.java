package com.xiaoyun.community.controller;

import com.xiaoyun.community.dto.AccessTokenDTO;
import com.xiaoyun.community.dto.GithubUser;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.provider.GithubHttpClentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GithubHttpClentProvider githubHttpClentProvider;

//    @Autowired
//    private GithubProvider githubProvider;

    @Value("${github-cliend-id}")
    private String githubCliendId;

    @Value("${github-cliend-secret}")
    private String githubClientSecret;

    @Value("${github-redirect-uri}")
    private String githubRedirectUri;

    @Autowired(required = false)
    private UserMapper userMapper;


    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setCliend_id(githubCliendId);
        accessTokenDTO.setClient_secret(githubClientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(githubRedirectUri);
        accessTokenDTO.setState(state);

        String accessToken = githubHttpClentProvider.getAccessToken(accessTokenDTO);
        log.info(accessToken);
        GithubUser githubUser = githubHttpClentProvider.getUser(accessToken);
        log.info(githubUser.toString());
        if (githubUser != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);

            response.addCookie(new Cookie("token",token));

            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }
}
