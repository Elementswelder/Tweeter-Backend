package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter{
    protected PagedView<T> view;
    protected T lastItem;
    protected boolean isLoading = false;
    protected boolean hasMorePages;

    public boolean hasMorePages(){
        return hasMorePages;
    }
    public boolean isLoading(){
        return isLoading;
    }
    public void setHasMorePages(boolean hasMorePages){
        this.hasMorePages = hasMorePages;
    }

    public PagedPresenter(PagedView view){
        super(view);
        this.view = view;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public interface PagedView<T> extends View{
        void setLoadingFooter(boolean value);
        void setIntent(User user);
        void addItems(List<T> items);
    }

    public void loadMoreItemsPaged(String username){
        followService.getUser(username, new MyGetSingleUserObserver());
        view.displayMessage("Getting user's profile...");
    }

    public void getMoreItemsPaged(User user){
        isLoading = true;
        view.setLoadingFooter(true);
        setupLoading(user);
    }

    public abstract void setupLoading(User user);

    protected class NewPagedObserver implements PagedObserver<T>{

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? (T) items.get(items.size() - 1) : null;
            view.addItems((List<T>) items);
            setHasMorePages(hasMorePages);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
            view.setLoadingFooter(false);
        }
    }


    protected class MyGetSingleUserObserver implements GetSingleUserObserver {
        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
            view.setLoadingFooter(false);
        }

        @Override
        public void handleSuccess(User user) {
            view.setIntent(user);
        }
    }
}
