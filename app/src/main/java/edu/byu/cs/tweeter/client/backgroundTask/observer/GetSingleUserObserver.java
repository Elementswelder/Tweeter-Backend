package edu.byu.cs.tweeter.client.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface GetSingleUserObserver extends ServiceObserver{
    void handleSuccess(User user);
}
