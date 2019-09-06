package com.luban.aspect;

import com.luban.annotation.LbTransactional;
import com.luban.transaction.Transaction;
import com.luban.transaction.TransactionMangage;
import com.luban.transaction.TransactionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(10000)
@Aspect
@Component
public class TransactionAspect {

    @Around("@annotation(com.luban.annotation.LbTransactional)")
    public void invoke(ProceedingJoinPoint proceedingJoinPoint){
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        LbTransactional annotation = signature.getMethod().getAnnotation(LbTransactional.class);
        String group = "";
        if (annotation.isStart()) {
            //创建事务组
           group  = TransactionMangage.createGroup();
        }else{
            //拿到当前事务组的ID
            //null
            group = TransactionMangage.getCurrent();
        }
        //他肯定存在分布式事物里面


        //创建事物对象
        Transaction transaction = TransactionMangage.createTransaction(group);
        
        //执行本地逻辑
        try {
            //Spring 会帮我们执行mysql的事物  一直等待
           proceedingJoinPoint.proceed();
            //提交本地事物状态  ---commit
            TransactionMangage.commitTransaction(transaction,annotation.isEnd(), TransactionType.COMMIT);
        } catch (Throwable throwable) {
            // 回滚
            TransactionMangage.commitTransaction(transaction,annotation.isEnd(), TransactionType.ROLLBACK);
            throwable.printStackTrace();

        }


    }

}
