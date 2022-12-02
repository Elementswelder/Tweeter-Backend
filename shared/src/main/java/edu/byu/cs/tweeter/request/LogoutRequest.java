package edu.byu.cs.tweeter.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a login request.
 */
public class LogoutRequest {

    private AuthToken authToken;
    private String userAlias;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LogoutRequest() {}

    
    public LogoutRequest(AuthToken authToken, String userAlias
    ) {
        this.userAlias = userAlias;
        this.authToken = authToken;
    }

    /**
     * Returns the username of the user to be logged in by this request.
     *
     * @return the username.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }


}
