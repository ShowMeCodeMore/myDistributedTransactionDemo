package com.luban.tack;

import com.luban.transaction.TransactionMangage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpUtil {

    private static RestTemplate restTemplate = new RestTemplate();


    public  static Object post(String url){
        HttpHeaders header = new HttpHeaders();
        header.set("groupId", TransactionMangage.getCurrent());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(null, header);
        return  restTemplate.postForObject(url,httpEntity,Object.class);

    }
}
