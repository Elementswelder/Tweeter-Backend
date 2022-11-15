package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class FollowHandler extends KingHandler implements RequestHandler<FollowRequest, FollowResponse>{
    @Override
    public FollowResponse handleRequest(FollowRequest followRequest, Context context) {
        FollowService followService = new FollowService(getFactoryInterface());

        return followService.followUser(followRequest);
    }
}
