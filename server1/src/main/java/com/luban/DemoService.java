package com.luban;

import com.luban.annotation.LbTransactional;
import com.luban.tack.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;


    //isStart
    //isEnd
    // @LbTransactional()
    @LbTransactional(isStart = true)
    @Transactional
    public void test() {
        demoDao.insert("server1");
        HttpUtil.post("http://localhost:8082/server2/test");
        int i = 1/0;
    }
}
