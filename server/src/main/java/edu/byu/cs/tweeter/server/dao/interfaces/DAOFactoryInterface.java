package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.S3DAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface DAOFactoryInterface {

    abstract FollowDAO getFollowDAO();

    abstract StatusDAO getStatusDAO();

    abstract FeedDAO getFeedDAO();

    abstract UserDAO getUserDAO();

    abstract StoryDAO getStoryDAO();

    abstract S3DAO getS3DAO();

    abstract AuthTokenDAO getAuthTokenDAO();
}
