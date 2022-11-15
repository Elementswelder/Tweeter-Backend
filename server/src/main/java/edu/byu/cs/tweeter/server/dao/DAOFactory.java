package edu.byu.cs.tweeter.server.dao;

public class DAOFactory implements DAOFactoryInterface{
    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new FeedDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new StoryDAO();
    }

    @Override
    public S3DAO getS3DAO() {
        return new S3DAO();
    }
}
