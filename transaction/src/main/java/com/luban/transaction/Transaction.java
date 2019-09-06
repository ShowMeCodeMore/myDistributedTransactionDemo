package com.luban.transaction;

import com.luban.tack.Task;

public class Transaction {

    private  String transactionId;

    private  String groupId;

    //事物状态  commit  还是 rollback

    private TransactionType transactionType;

    private Task task;

    public Transaction(String transactionId, String groupId) {
        this.transactionId = transactionId;
        this.groupId = groupId;
        task = new Task();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
