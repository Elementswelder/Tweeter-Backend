package edu.byu.cs.tweeter.response;

import edu.byu.cs.tweeter.request.LoginRequest;

/**
 * A response for a {@link LoginRequest}.
 */
public class IsFollowerResponse extends Response {

    boolean follower;

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(boolean follower) {
        super(true, null);
        this.follower = follower;
    }
}

