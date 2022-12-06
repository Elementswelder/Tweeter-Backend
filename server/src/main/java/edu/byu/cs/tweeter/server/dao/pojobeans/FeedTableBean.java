package edu.byu.cs.tweeter.server.dao.pojobeans;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FeedTableBean {

    public String post;
    public String time_stamp;
    public List<String> urls;
    public List<String> mentions;
    public String user_alias;
    public String user_feed;


    public FeedTableBean(){}

    public FeedTableBean(String post, String time_stamp, List<String> urls, List<String> mentions,
                         String user_alias,String user_feed) {
        this.post = post;
        this.time_stamp = time_stamp;
        this.urls = urls;
        this.mentions = mentions;
        this.user_alias = user_alias;
        this.user_feed = user_feed;
    }

    @DynamoDbPartitionKey
    public String getUser_feed() {
        return user_feed;
    }

    public void setUser_feed(String user_feed) {
        this.user_feed = user_feed;
    }

    public String getUser_alias() {
        return user_alias;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @DynamoDbSortKey
    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String timestamp) {
        this.time_stamp = timestamp;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public void setUser_alias(String alias) {
        this.user_alias = alias;
    }
}
