package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.response.StatusResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAOInterface {

    abstract Pair<List<Status>, Boolean> getStatuses(StatusRequest request);
    abstract PostStatusResponse postStatus(PostStatusRequest request);

}
