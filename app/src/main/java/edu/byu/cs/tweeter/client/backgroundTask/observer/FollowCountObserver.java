package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface FollowCountObserver extends ServiceObserver {
    void handleSuccess(String followNum);
}
