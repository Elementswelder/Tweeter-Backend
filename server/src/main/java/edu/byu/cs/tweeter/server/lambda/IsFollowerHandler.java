package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class IsFollowerHandler extends KingHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {
    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest loginRequest, Context context) {
        FollowService followService = new FollowService(getFactoryInterface());
        IsFollowerResponse response = followService.isFollowerResponse(loginRequest);
        System.out.println("Reponse is: "+ response.isFollower());
        return response;
    }
}
