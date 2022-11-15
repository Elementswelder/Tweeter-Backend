package edu.byu.cs.tweeter.server.dao;

public interface DAOFactoryInterface {
    abstract FollowDAO getFollowDAO();

    abstract StatusDAO getStatusDAO();

    abstract FeedDAO getFeedDAO();

    abstract UserDAO getUserDAO();

    abstract StoryDAO getStoryDAO();

    abstract S3DAO getS3DAO();
}
