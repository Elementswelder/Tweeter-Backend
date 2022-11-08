package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTaskServer;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Handles messages from the background task indicating that the task is done, by invoking
 * methods on the observer.
 */
public class GetFollowingTaskHandler extends Handler {

    private final FollowService.GetFollowingObserver observer;

    public GetFollowingTaskHandler(FollowService.GetFollowingObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(GetFollowingTaskServer.SUCCESS_KEY);
        if (success) {
            List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTaskServer.FOLLOWEES_KEY);
            boolean hasMorePages = bundle.getBoolean(GetFollowingTaskServer.MORE_PAGES_KEY);
            observer.handleSuccess(followees, hasMorePages);
        } else if (bundle.containsKey(GetFollowingTaskServer.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(GetFollowingTaskServer.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(GetFollowingTaskServer.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(GetFollowingTaskServer.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
