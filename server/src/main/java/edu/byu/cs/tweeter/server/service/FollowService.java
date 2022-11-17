package edu.byu.cs.tweeter.server.service;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private DAOFactoryInterface factoryInterface;


    public FollowService(DAOFactoryInterface factoryInterface){
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
    public FollowingResponse getFollowees(FollowingRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowees(request);
    }

    public FollowerResponse getFollowers(FollowersRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowers(request);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        return getFollowingDAO().getFollowingCount(request);
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        return getFollowingDAO().getFollowerCount(request);
    }

    public IsFollowerResponse isFollowerResponse(IsFollowerRequest request){
        return getFollowingDAO().isFollower(request);
    }

    public FollowResponse followUser(FollowRequest request){
        return getFollowingDAO().followUser(request);
    }

    public UnfollowResponse unFollowUser(UnfollowRequest request){
        return getFollowingDAO().unFollowUser(request);
    }


        /**
         * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
         * for testing purposes. All usages of FollowDAO should get their FollowDAO
         * instance from this method to allow for mocking of the instance.
         *
         * @return the instance.
         */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    FakeData getFakeData() {return FakeData.getInstance();}
}
