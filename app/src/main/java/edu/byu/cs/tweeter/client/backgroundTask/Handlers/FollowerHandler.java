package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowerObserver;

public class FollowerHandler extends BackgroundTaskHandler<FollowerObserver>{
    public FollowerHandler(FollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowerObserver observer, Bundle data) {
        Boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
