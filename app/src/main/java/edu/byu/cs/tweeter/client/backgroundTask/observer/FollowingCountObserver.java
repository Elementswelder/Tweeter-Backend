package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface FollowingCountObserver extends ServiceObserver {
    void handleSuccess(String followNum);
}
