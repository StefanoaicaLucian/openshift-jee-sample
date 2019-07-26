package com.tutorialspoint.demo;

import java.util.ArrayList;
import java.util.List;

public class PageResponse {

    private List<Message> messages = new ArrayList<>();

    private long totalRecordsCount;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public long getTotalRecordsCount() {
        return totalRecordsCount;
    }

    public void setTotalRecordsCount(long totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }
}
