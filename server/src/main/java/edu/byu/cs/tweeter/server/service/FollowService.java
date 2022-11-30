package edu.byu.cs.tweeter.server.service;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.response.FollowResponse;
import edu.byu.cs.tweeter.response.FollowerCountResponse;
import edu.byu.cs.tweeter.response.FollowingCountResponse;
import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.response.UnfollowResponse;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.response.FollowerResponse;
import edu.byu.cs.tweeter.response.FollowingResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
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
        // if (!checkValidAuth(request.getAuthToken().getToken())){
        //   return new GetUserResponse("AuthToken Expired, please log in again");
        //}

        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return factoryInterface.getFollowDAO().getFollowees(request);
    }

    public FollowerResponse getFollowers(FollowersRequest request) {
        // if (!checkValidAuth(request.getAuthToken().getToken())){
        //   return new GetUserResponse("AuthToken Expired, please log in again");
        //}
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return factoryInterface.getFollowDAO().getFollowers(request);
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
