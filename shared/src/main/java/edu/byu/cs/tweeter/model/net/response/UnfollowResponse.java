package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class UnfollowResponse extends Response {

    private AuthToken authToken;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public UnfollowResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the now logged in user.
     * @param authToken the auth token representing this user's session with the server.
     */
    public UnfollowResponse(AuthToken authToken) {
        super(true, null);
        this.authToken = authToken;
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
