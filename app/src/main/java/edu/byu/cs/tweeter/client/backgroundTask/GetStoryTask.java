package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public AuthToken authToken;
    public User user;
    public int limit;
    public Status lastStatus;
    public List<Status> items;
    public boolean hasMorePages;

    private static final String LOG_TAG = "GetStoryTask";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
        this.authToken = authToken;
        this.user = targetUser;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        return new Pair<>(this.items, this.hasMorePages);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastStatus = lastItem == null ? null : lastItem.getPost();

            StatusRequest request = new StatusRequest(authToken, targetUserAlias, limit, lastStatus);
            StatusResponse response = serverFacade.getStatus(request, StatusService.URL_LOAD_STATUS);

            if (response.isSuccess()) {
                this.items = response.getStatuses();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get statuses", ex);

        }
    }

    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) this.items);
        msgBundle.putBoolean(MORE_PAGES_KEY, this.hasMorePages);
    }
}
