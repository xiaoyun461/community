package com.xiaoyun.community;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.stream.Collectors;


@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {
        String[] strs = StringUtils.split("第一, 第二,第三", ",");

        String collect = Arrays.stream(strs).collect(Collectors.joining("'|'", "'", "'"));

        System.out.println(collect);

    }

}
