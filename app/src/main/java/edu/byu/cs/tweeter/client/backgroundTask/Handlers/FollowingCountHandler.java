package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowingCountObserver;

public class FollowingCountHandler extends BackgroundTaskHandler<FollowingCountObserver> {
    public FollowingCountHandler(FollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowingCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleSuccess(String.valueOf(count));
    }
}
