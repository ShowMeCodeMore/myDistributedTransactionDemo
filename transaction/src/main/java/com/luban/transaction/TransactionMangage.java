package com.luban.transaction;

import com.alibaba.fastjson.JSONObject;
import com.luban.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TransactionMangage {

    private  static ThreadLocal<String>  current = new ThreadLocal<>();
    private  static ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();

    private static final   Map<String,Map<String,Transaction>> GROUP_MAP = new HashMap<>();


    private static NettyClient nettyClient;


    @Autowired
    public  void setNettyClient(NettyClient nettyClient){
        this.nettyClient = nettyClient;
    }

    //创建事务组
    public static String createGroup(){
        String id = UUID.randomUUID().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId",id);
        jsonObject.put("command","create");
        //发送给Netty
        current.set(id);
        GROUP_MAP.put(id,new HashMap<>());
        nettyClient.send(jsonObject);
        return  id;
    }

    //创建事物对象

    public static  Transaction createTransaction(String groupId){
        String uuid = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(uuid,groupId);
       if ( GROUP_MAP.get(groupId)==null){
           GROUP_MAP.put(groupId,new HashMap<>());
       }
        currentTransaction.set(transaction);
        GROUP_MAP.get(groupId).put(uuid,transaction);
        return  transaction;
    }


    //提交本地事物
    public static void commitTransaction(Transaction transaction,boolean isEnd,TransactionType transactionType){
        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("groupId",transaction.getGroupId());
        jsonObject.put("transactionId",transaction.getTransactionId());
        jsonObject.put("transactionType",transactionType);
        jsonObject.put("command","add");
        jsonObject.put("isEnd",isEnd);
        nettyClient.send(jsonObject);
        System.out.println("执行了添加事务");

    }


    public static Transaction getTransactionById(String groupId,String id){
        return GROUP_MAP.get(groupId).get(id);
    }

    public static Transaction getCurrentTransaction(){
        return  currentTransaction.get();
    }


    public static String getCurrent(){

        return  current.get();
    }

    public static void setCurrent(String groupId){

        current.set(groupId);
    }

}
