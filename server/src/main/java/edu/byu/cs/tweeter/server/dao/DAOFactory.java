package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.interfaces.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.S3DAOInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.StatusDAOInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.StoryDAOInterface;
import edu.byu.cs.tweeter.server.dao.interfaces.UserDAOInterface;

public class DAOFactory implements DAOFactoryInterface {

    StatusDAOInterface statusDAOInterface = new StatusDAO();
    AuthTokenDAOInterface authTokenDAOInterface = new AuthTokenDAO();
    UserDAOInterface userDAOInterface = new UserDAO();
    StoryDAOInterface storyDAOInterface = new StoryDAO();
    S3DAOInterface s3DAOInterface = new S3DAO();
    FollowDAOInterface followDAOInterface = new FollowDAO();


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
        return (UserDAO) userDAOInterface;
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
