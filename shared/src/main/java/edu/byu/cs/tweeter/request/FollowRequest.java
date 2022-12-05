package edu.byu.cs.tweeter.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a login request.
 */
public class FollowRequest {

    private User followee;
    private User currentUser;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */

    public FollowRequest() {}

    public FollowRequest(AuthToken authToken, User follower, User currentUser) {
        this.authToken = authToken;
        this.followee = follower;
        this.currentUser = currentUser;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
