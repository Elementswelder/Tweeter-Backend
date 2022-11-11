package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private User user;
    private AuthToken authToken;
    private static final String LOG_TAG = "GetFollowerCount";
    public static final String COUNT_KEY = "count";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private int count;

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
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

            FollowerCountRequest request = new FollowerCountRequest(authToken, user);
            FollowerCountResponse response = serverFacade.getFollowerCount(request, FollowService.URL_GET_FOLLOWER_COUNT);

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


