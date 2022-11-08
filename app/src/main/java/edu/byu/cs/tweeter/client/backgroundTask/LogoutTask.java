package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    private AuthToken authToken;
    private static final String LOG_TAG = "LogoutTask";


    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        this.authToken = authToken;
    }

    @Override
    protected void runTask() {

        try {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
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
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        Cache.getInstance().clearCache();
        // Call sendSuccessMessage if successful
        sendSuccessMessage();
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
    }
}
