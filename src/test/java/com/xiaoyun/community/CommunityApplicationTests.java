package com.xiaoyun.community;


import com.xiaoyun.community.provider.FTPUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {
        String[] strs = StringUtils.split("第一, 第二,第三", ",");

        String collect = Arrays.stream(strs).collect(Collectors.joining("'|'", "'", "'"));

        System.out.println(collect);
    }
    @Test
    public  void  test(){
        System.out.println(TimeUnit.DAYS.toMillis(10L));
        System.out.println(TimeUnit.MINUTES.toDays(10L));
    }

    @Test
    public void uploadFile() throws Exception {
        File file = new File("F:\\xiaoyun461\\community\\src\\main\\resources\\static\\images\\xy461.jpg");
//        System.out.println(file.getName());
        boolean b = FTPUtils.uploadFile("jita.jpg", new FileInputStream(file));
        Assert.assertEquals(true, false);
//        InputStream ftpClient = FTPUtils.getInputStream("/*");
//        ftpClient.read();

    }

}
