package edu.byu.cs.tweeter.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class IsFollowerResponse extends Response {

    private boolean follower;
    private AuthToken authToken;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(boolean follower, AuthToken authToken) {
        super(true, null);
        this.follower = follower;
        this.authToken = authToken;
    }

    public boolean getfollower() {
        return follower;
    }

    public void setfollower(boolean follower) {
        follower = follower;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the auth token.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }
}
