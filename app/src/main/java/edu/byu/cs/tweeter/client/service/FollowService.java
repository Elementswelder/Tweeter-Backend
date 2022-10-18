package edu.byu.cs.tweeter.client.service;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.FollowCountHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.FollowerHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.GetSingleUserHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends ServiceHandler<Runnable>{

    public void loadMoreItems(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken, user,
                pageSize, lastFollowee, new PagedNotificationHandler<>(observer));
        startTask(getFollowingTask);
    }

    public void loadMoreItemsFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedNotificationHandler<>(observer));
        startTask(getFollowersTask);
    }

    public void loadMoreItemsFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> observer){
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedNotificationHandler<>(observer));
        startTask(getFeedTask);
    }

    public void getUser(String username, GetSingleUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                username, new GetSingleUserHandler(observer));
        startTask(getUserTask);
    }

    public void getFollowStatus(User user, FollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), user, new FollowerHandler(observer));
        startTask(isFollowerTask);
    }

    public void followUser(User selectedUser, SimpleNotifyObserver observer){
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleNotificationHandler(observer));
        startTask(followTask);

    }

    public void unfollowUser(User selectedUser, SimpleNotifyObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleNotificationHandler(observer));
        startTask(unfollowTask);
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser, FollowCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowCountHandler(observer));
        startTask(followersCountTask);

        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowCountHandler(observer));
        startTask(followingCountTask);
    }
}