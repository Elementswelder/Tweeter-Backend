package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.net.TweeterRemoteException;
import edu.byu.cs.tweeter.response.FollowingCountResponse;
import edu.byu.cs.tweeter.request.FollowingCountRequest;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class  GetFollowingCountTask extends GetCountTask {

    private int count;
    private User user;
    private AuthToken authToken;
    private static final String LOG_TAG = "GetFollowingCount";
    public static final String COUNT_KEY = "count";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
        this.user = targetUser;
        this.authToken = authToken;
    }

    @Override
    protected int runCountTask() {
        return count;
    }

    @Override
    protected void runTask() {
        try {

            FollowingCountRequest request = new FollowingCountRequest(authToken, user);
            FollowingCountResponse response = serverFacade.getFollowingCount(request, FollowService.URL_GET_FOLLOWING_COUNT);

            if (response.isSuccess()) {
                count = response.getCount();
                authToken = response.getAuthToken();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get statuses", ex);

        }
    }

    protected void loadSuccessBundle(Bundle msgBundle){
        msgBundle.putSerializable(COUNT_KEY, this.count);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}
