package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;

public class FollowCountHandler extends BackgroundTaskHandler<FollowCountObserver> {
    public FollowCountHandler(FollowCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleSuccess(String.valueOf(count));
    }
}
