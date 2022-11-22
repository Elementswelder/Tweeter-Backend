package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.response.StatusResponse;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {

    private DAOFactoryInterface factoryInterface;


    public StatusService(DAOFactoryInterface factoryInterface){
        this.factoryInterface = factoryInterface;
    }
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public StatusResponse getStatuses(StatusRequest request) {
        if (request.getLastStatusString() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getStatuses(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if (request.getLastStatusString() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if (request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a completed status");
        }
        return getStatusDAO().postStatus(request);
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    FakeData getFakeData() {return FakeData.getInstance();}
}
