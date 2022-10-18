package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;

public class SimpleNotificationHandler extends BackgroundTaskHandler<SimpleNotifyObserver> {

    public SimpleNotificationHandler(SimpleNotifyObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(SimpleNotifyObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
