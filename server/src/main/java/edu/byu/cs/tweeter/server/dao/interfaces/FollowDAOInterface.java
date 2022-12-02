package edu.byu.cs.tweeter.server.dao.interfaces;

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
import edu.byu.cs.tweeter.util.Pair;

public interface FollowDAOInterface {

    abstract IsFollowerResponse isFollower(IsFollowerRequest request);
    abstract FollowResponse followUser(FollowRequest request);
    abstract UnfollowResponse unFollowUser(UnfollowRequest request);
    abstract Pair<List<User>, Boolean> getFollowees(FollowingRequest request);
    abstract Pair<List<User>, Boolean> getFollowers(FollowersRequest request);



}
