package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class PostStatusHandler extends KingHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the followees.
     */
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService service = new StatusService(getFactoryInterface());
        System.out.println(request.getStatus());
        SqsClient.sendMessage(SqsClient.getPostStatusQueueUrl(),
                new Gson().toJson(request.getStatus()));
        return service.postStatus(request);
    }
}
