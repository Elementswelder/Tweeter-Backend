package edu.byu.cs.tweeter.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a login request.
 */
public class FollowerCountRequest {

    private AuthToken authToken;
    private User user;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowerCountRequest() {}


    public FollowerCountRequest(AuthToken authToken, User user) {
        this.authToken = authToken;
        this.user = user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
