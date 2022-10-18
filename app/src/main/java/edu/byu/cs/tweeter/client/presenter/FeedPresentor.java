package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresentor extends PagedPresenter<Status> {


    public FeedPresentor(PagedView<Status> view){
        super(view);
    }

    @Override
    public void setupLoading(User user) {
        followService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItem, new NewPagedObserver());
    }
}
