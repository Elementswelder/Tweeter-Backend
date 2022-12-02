package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetUserHandler extends KingHandler implements RequestHandler<GetUserRequest, GetUserResponse> {

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
    public GetUserResponse handleRequest(GetUserRequest request, Context context) {
        UserService service = new UserService(getFactoryInterface());
        GetUserResponse response = service.getUser(request);
        return response;
    }
}
