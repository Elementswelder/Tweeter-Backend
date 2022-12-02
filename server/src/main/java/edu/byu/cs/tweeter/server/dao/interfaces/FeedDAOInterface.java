package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.response.FeedResponse;

public interface FeedDAOInterface {
    abstract FeedResponse getFeed(FeedRequest request);

}
