package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.LoginsObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginsHandler extends BackgroundTaskHandler<LoginsObserver> {
    public LoginsHandler(LoginsObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(LoginsObserver observer, Bundle data) {
        User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);
        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        observer.handleSuccess(loggedInUser, authToken);
    }
}
