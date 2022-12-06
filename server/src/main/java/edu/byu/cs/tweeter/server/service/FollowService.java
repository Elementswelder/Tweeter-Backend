package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.request.UnfollowRequest;
import edu.byu.cs.tweeter.response.FollowResponse;
import edu.byu.cs.tweeter.response.FollowerResponse;
import edu.byu.cs.tweeter.response.FollowingResponse;
import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends KingService {



    public FollowService(DAOFactoryInterface factoryInterface){
        super(factoryInterface);
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
        if (!checkValidAuth(request.getAuthToken().getToken())){
         return new FollowingResponse("AuthToken Expired, please log in again");
        }

        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<User>, Boolean> followerUsers = factoryInterface.getFollowDAO().getFollowees(request);
        if (followerUsers == null){
            return new FollowingResponse("COULD NOT GET THE FOLLOWEES");
        }
        FollowingResponse response = new FollowingResponse(followerUsers.getFirst(), followerUsers.getSecond());
        return response;
    }

    public FollowerResponse getFollowers(FollowersRequest request) {
        if (!checkValidAuth(request.getAuthToken().getToken())){
          return new FollowerResponse("AuthToken Expired, please log in again");
        }
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<User>, Boolean> followeesPair = factoryInterface.getFollowDAO().getFollowers(request);
        if (followeesPair == null){
            return new FollowerResponse("COULD NOT GET THE FOLLOWERS");
        }
        return new FollowerResponse(followeesPair.getFirst(), followeesPair.getSecond());
    }


    public IsFollowerResponse isFollowerResponse(IsFollowerRequest request){
       // if (!checkValidAuth(request.getAuthToken().getToken())){
      //    return new IsFollowerResponse("AuthToken Expired, please log in again");
      //  }
        return factoryInterface.getFollowDAO().isFollower(request);
    }

    public FollowResponse followUser(FollowRequest request){
        //if (!checkValidAuth(request.getAuthToken().getToken())){
      //       return new FollowResponse("AuthToken Expired, please log in again");
       // }
        boolean success = factoryInterface.getUserDAO().updateFollowCount(request.getCurrentUser(), request.getFollowee(), true);
        if (!success){
            return new FollowResponse("FAILED TO UPDATE THE FOLLOW COUNT IN USER DAO");
        }
        //Add the follower row to the table
        else{
            FollowResponse response = factoryInterface.getFollowDAO().followUser(new FollowRequest(request.getAuthToken(), request.getFollowee(), request.getCurrentUser()));
            if (response.isSuccess()){
                return response;
            }
        }
        return new FollowResponse("Failed to add the user to the table");
    }

    public UnfollowResponse unFollowUser(UnfollowRequest request){
        if (!checkValidAuth(request.getAuthToken().getToken())){
            return new UnfollowResponse("AuthToken Expired, please log in again");
        }
        boolean success = factoryInterface.getUserDAO().updateFollowCount(request.getCurrentUser(), request.getFollowee(), false);
        if (!success){
            return new UnfollowResponse("FAILED TO UPDATE THE FOLLOW COUNT IN USER DAO");
        }
        return factoryInterface.getFollowDAO().unFollowUser(request);
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
}
