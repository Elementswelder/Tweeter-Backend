package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.time.LocalDate;

import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.request.RegisterRequest;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {

    /**
     * The user's first name.
     */
    private final String firstName;
    
    /**
     * The user's last name.
     */
    private final String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private final String image;

    private static final String LOG_TAG = "RegisterTask";
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";
    private User registeredUser;
    private AuthToken authToken;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler, username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {;
        return new Pair<>(this.registeredUser, this.authToken);
    }

    @Override
    protected void runTask() {
        try {

            RegisterRequest request = new RegisterRequest(firstName, lastName, image, username, password);
            RegisterResponse response = serverFacade.register(request, UserService.URL_REGISTER);

            if (response.isSuccess()) {
                registeredUser = response.getUser();
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
        msgBundle.putSerializable(USER_KEY, this.registeredUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}
