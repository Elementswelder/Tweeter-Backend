package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    protected User loggedUser;
    private static final String LOG_TAG = "LoginTask";
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    protected AuthToken authToken;

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        runTask();
        User loggedInUser = this.loggedUser;
        AuthToken authToken = this.authToken;
        return new Pair<>(loggedInUser, authToken);
    }

    protected void loadSuccessBundle(Bundle msgBundle){
        msgBundle.putSerializable(USER_KEY, this.loggedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }

    @Override
    protected void runTask() {
        try {
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = serverFacade.login(request, UserService.URL_PATH);

            if (response.isSuccess()) {
                this.loggedUser = response.getUser();
                this.authToken = response.getAuthToken();
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
