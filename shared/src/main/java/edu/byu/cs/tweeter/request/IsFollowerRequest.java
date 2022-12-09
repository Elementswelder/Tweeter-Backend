package edu.byu.cs.tweeter.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a login request.
 */
public class IsFollowerRequest {

    private User currentUser;
    private User followee;
    private AuthToken auth_token;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken auth_token, User currentUser, User followee) {
        this.auth_token = auth_token;
        this.followee = followee;
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }



    public AuthToken getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(AuthToken auth_token) {
        this.auth_token = auth_token;
    }
}
