package edu.byu.cs.tweeter.server.dao.pojobeans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowsTableBean {

    public String follower_handle;
    public String follower_first_name;
    public String follower_last_name;
    public String follower_image;
    public String followee_handle;
    public String following_first_name;
    public String following_last_name;
    public String following_image;


    public FollowsTableBean(){

    }
    public FollowsTableBean(String follower_handle, String follower_first_name, String follower_last_name,
                            String follower_image, String followee_handle, String following_first_name,
                            String following_last_name, String following_image){
        this.followee_handle = followee_handle;
        this.following_first_name = following_first_name;
        this.following_last_name = following_last_name;
        this.following_image = following_image;
        this.follower_handle = follower_handle;
        this.follower_first_name = follower_first_name;
        this.follower_last_name =  follower_last_name;
        this.follower_image = follower_image;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = {"followIndex"})
    public String getFollower_handle() {
        return follower_handle;
    }

    public String getFollower_first_name() {
        return follower_first_name;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = { "followIndex"})
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    public void setFollower_first_name(String follower_first_name) {
        this.follower_first_name = follower_first_name;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    public void setFollowee_name(String followee_name) {
        this.following_first_name = followee_name;
    }

    public String getFollowee_name() {
        return following_first_name;
    }

    public String getFollower_last_name() {
        return follower_last_name;
    }

    public void setFollower_last_name(String follower_last_name) {
        this.follower_last_name = follower_last_name;
    }

    public String getFollower_image() {
        return follower_image;
    }

    public void setFollower_image(String follower_image) {
        this.follower_image = follower_image;
    }

    public String getFollowing_first_name() {
        return following_first_name;
    }

    public void setFollowing_first_name(String following_first_name) {
        this.following_first_name = following_first_name;
    }

    public String getFollowing_last_name() {
        return following_last_name;
    }

    public void setFollowing_last_name(String following_last_name) {
        this.following_last_name = following_last_name;
    }

    public String getFollowing_image() {
        return following_image;
    }

    public void setFollowing_image(String following_image) {
        this.following_image = following_image;
    }
}
