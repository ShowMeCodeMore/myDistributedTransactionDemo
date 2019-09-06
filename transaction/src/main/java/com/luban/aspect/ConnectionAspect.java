package com.luban.aspect;

import com.luban.connection.LbConnection;
import com.luban.transaction.TransactionMangage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class ConnectionAspect {


    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint proceedingJoinPoint){
        try {
//            return (Connection) proceedingJoinPoint.proceed();
            return new LbConnection((Connection) proceedingJoinPoint.proceed(), TransactionMangage.getCurrentTransaction());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return  null;
    }

}
