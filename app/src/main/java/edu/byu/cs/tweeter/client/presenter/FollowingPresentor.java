package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresentor extends PagedPresenter<User>{

    private PagedView<User> view;

    public FollowingPresentor(PagedView<User> view) {
        super(view);
        this.view = view;
    }

    @Override
    public void setupLoading(User user) {
        followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItem, new NewPagedObserver());
    }
}
