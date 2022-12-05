package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.net.TweeterRemoteException;
import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    public static final String IS_FOLLOWER_KEY = "is-follower";
    private static final String LOG_TAG = "IsFollower";

    /**
     * The alleged follower.
     */
    private final User currentUser;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean follower;
    private AuthToken authToken;

    public IsFollowerTask(AuthToken authToken, User currentUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.currentUser = currentUser;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {

            IsFollowerRequest request = new IsFollowerRequest(authToken, currentUser, followee);
            System.out.println("INSIDE OTHER HUNK OF JUNK + " + request.getCurrentUser().getAlias() + " " + request.getFollowee().getAlias());
            IsFollowerResponse response = serverFacade.isFollower(request, FollowService.URL_IS_FOLLOWER);

            if (response.isSuccess()) {
                System.out.println("INSIDE THIS HUNK OF JUNK " + follower);
                authToken = response.getAuthToken();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get statuses", ex);

        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, follower);
    }
}
