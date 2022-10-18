package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface FollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
