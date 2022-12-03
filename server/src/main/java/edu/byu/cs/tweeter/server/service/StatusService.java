package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.response.StatusResponse;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

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
        if (request.getLastStatusTime() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<Status>, Boolean> allStories = null;
        try {
            allStories = factoryInterface.getStatusDAO().getStatuses(request);
            GetUserResponse currentUser = factoryInterface.getUserDAO().getUser(
                    new GetUserRequest(request.getAuthToken(), request.getFollowerAlias()));

            for(int i = 0; i < allStories.getFirst().size(); i++){
                System.out.println("Added story number: " + i);
                allStories.getFirst().get(i).setUser(currentUser.getUser());
            }
            return new StatusResponse(allStories.getFirst(), allStories.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            return new StatusResponse("FAILED TO ADD USERES TO THE STATUS - STATUSSERVICE");
        }
    }

    public FeedResponse getFeed(FeedRequest request) {
        if (request.getLastStatusString() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<Status>, Boolean> list = factoryInterface.getFeedDAO().getFeed(request);

        if (list == null){
            return new FeedResponse("FAILED TO GET FEED IN THE GETFEED");
        }
        return new FeedResponse(list.getFirst(), list.getSecond());
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if (request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a completed status");
        }
        return factoryInterface.getStatusDAO().postStatus(request);
    }

    StoryDAO getStatusDAO() {
        return new StoryDAO();
    }

    FakeData getFakeData() {return FakeData.getInstance();}
}
