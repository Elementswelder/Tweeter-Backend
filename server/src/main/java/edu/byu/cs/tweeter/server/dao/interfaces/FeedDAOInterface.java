package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAOInterface {
    abstract Pair<List<Status>, Boolean> getFeed(FeedRequest request);

}
