package com.xiaoyun.community.dto;

import lombok.Data;

@Data
public class AccessTokenDTO {
    private String cliend_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
