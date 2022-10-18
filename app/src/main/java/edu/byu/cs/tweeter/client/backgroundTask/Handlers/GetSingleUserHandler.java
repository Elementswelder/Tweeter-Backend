package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetSingleUserHandler extends BackgroundTaskHandler<GetSingleUserObserver>{
    public GetSingleUserHandler(GetSingleUserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetSingleUserObserver observer, Bundle data) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }
}
