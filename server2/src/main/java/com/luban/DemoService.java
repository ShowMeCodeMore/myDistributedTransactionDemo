package com.luban;

import com.luban.annotation.LbTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;


    @LbTransactional(isEnd = true)
    @Transactional
    public void test() {
        demoDao.insert("server2");
    }
}
