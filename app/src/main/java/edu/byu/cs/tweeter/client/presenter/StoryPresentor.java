package edu.byu.cs.tweeter.client.presenter;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresentor extends PagedPresenter<Status>{


    public StoryPresentor(PagedView<Status> view){
        super(view);
    }

    @Override
    public void setupLoading(User user) {
        statusService.loadMoreStatus(Cache.getInstance().getCurrUserAuthToken(),
                user, 10, lastItem, new NewPagedObserver());
    }
}
