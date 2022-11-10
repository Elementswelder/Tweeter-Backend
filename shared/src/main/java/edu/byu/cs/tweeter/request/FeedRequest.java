package edu.byu.cs.tweeter.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followees for a specified follower.
 */
public class FeedRequest {

    private AuthToken authToken;
    private String followerAlias;
    private int limit;
    private String lastStatusString;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FeedRequest() {}


    public FeedRequest(AuthToken authToken, String followerAlias, int limit, String lastStatusString) {
        this.authToken = authToken;
        this.followerAlias = followerAlias;
        this.limit = limit;
        this.lastStatusString = lastStatusString;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the follower whose followees are to be returned by this request.
     *
     * @return the follower.
     */
    public String getLastStatusString() {
        return followerAlias;
    }

    /**
     * Sets the follower.
     *
     * @param followerAlias the follower.
     */
    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    /**
     * Returns the number representing the maximum number of followees to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the last followee that was returned in the previous request or null if there was no
     * previous request or if no followees were returned in the previous request.
     *
     * @return the last followee.
     */
    public String lastStatusString() {
        return lastStatusString;
    }


    public void setLastStatusString(String lastStatusString) {
        this.lastStatusString = lastStatusString;
    }
}
