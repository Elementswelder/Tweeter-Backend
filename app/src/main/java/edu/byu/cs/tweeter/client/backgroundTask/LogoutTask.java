package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.cache.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.request.LogoutRequest;
import edu.byu.cs.tweeter.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    private AuthToken authToken;
    private String userAlias;
    private static final String LOG_TAG = "LogoutTask";


    public LogoutTask(AuthToken authToken, String userAlias, Handler messageHandler) {
        super(authToken, messageHandler);
        this.userAlias = userAlias;
        this.authToken = authToken;
    }

    @Override
    protected void runTask() {

        try {
            LogoutRequest logoutRequest = new LogoutRequest(authToken, userAlias);
            LogoutResponse response = serverFacade.logout(logoutRequest, UserService.URL_LOGOUT);

            if (response.isSuccess()) {
                Cache.getInstance().clearCache();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }
}
