package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.pojobeans.FeedTableBean;

public class FeedList {

    private List<FeedTableBean> feedList = new ArrayList<>();

    public FeedList(){}

    public FeedList(List<FeedTableBean> list){
        this.feedList = list;
    }

    public List<FeedTableBean> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<FeedTableBean> feedList) {
        this.feedList = feedList;
    }
}
