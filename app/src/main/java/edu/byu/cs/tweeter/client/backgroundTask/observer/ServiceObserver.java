package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);
}