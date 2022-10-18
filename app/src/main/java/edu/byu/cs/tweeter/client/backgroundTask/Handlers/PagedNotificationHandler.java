package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;

//4 Cases of a list of items
public  class PagedNotificationHandler<T> extends BackgroundTaskHandler<PagedObserver<T>> {



    public PagedNotificationHandler(PagedObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedObserver<T> observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
