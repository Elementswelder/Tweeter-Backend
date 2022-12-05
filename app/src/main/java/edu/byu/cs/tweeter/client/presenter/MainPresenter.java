package edu.byu.cs.tweeter.client.presenter;

import android.view.MenuItem;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter  {

    private MainView view;

    public interface MainView extends View {
        void setFollowerButton(boolean isFollower);
        void setFollowingText(String text);
        void setFollowerText(String text);
        void logout();
        void setFollowerButtonVisable();
        void setFollowerButtonGone();
    }

    public MainPresenter(MainView view){
        super(view);
        this.view = view;
    }


    public void setFollowingStatus(User selectedUser) {
        followService.getFollowStatus(selectedUser, new MyFollowerObserver());

    }
    public void setFollowButton(String buttonText, User selectedUser) {
        if (buttonText.equals("Following")) {
            followService.unfollowUser(selectedUser, new MySimpleNotifyObserver(selectedUser));
            view.displayMessage("Removing " + selectedUser.getName() + "...");

        } else {
            followService.followUser(selectedUser, new SimpleNotifyObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void handleException(Exception ex) {
                    view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                }

                @Override
                public void handleSuccess() {
                    followService.updateSelectedUserFollowingAndFollowers(selectedUser, new MyFollowCountObserver());
                }
            });
            setFollowingButton(true);
            view.displayMessage("Adding " + selectedUser.getName() + "...");
        }
    }

    private void setFollowingButton(boolean isFollowed) {
        view.setFollowerButton(isFollowed);
    }

    public void updateSelectedUserFollowingAndFollowers(User user) {
        followService.updateSelectedUserFollowingAndFollowers(user, new MyFollowCountObserver());
    }

    public void createNewStatus(String post) {
        getStatusService().createNewPost(post, new SimpleNotifyObserver() {
            @Override
            public void handleSuccess() {
                view.displayMessage("Successfully Posted!");
            }

            @Override
            public void handleFailure(String message) {
                view.displayMessage(message);
            }

            @Override
            public void handleException(Exception exception) {
                view.displayMessage(exception.toString());
            }
        });
    }

    public boolean attemptLogout(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            view.displayMessage("Logging Out...");
            userService.logout(new SimpleNotifyObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage(message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.displayMessage(exception.toString());
                }

                @Override
                public void handleSuccess() {
                    view.logout();
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public void checkUserNull(User selectedUser) {
        if (selectedUser == null){
            view.displayMessage("User NOT passed to activity");
        }
    }


    public void checkFollowButton(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowerButtonGone();
        } else {
            view.setFollowerButtonVisable();
            setFollowingStatus(selectedUser);
        }
    }

    private class MyFollowerObserver implements FollowerObserver {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.setFollowerButton(isFollower);
        }
    }

    private class MySimpleNotifyObserver implements SimpleNotifyObserver {
        private final User selectedUser;

        public MySimpleNotifyObserver(User selectedUser) {
            this.selectedUser = selectedUser;
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess() {
            view.setFollowerButton(false);
            followService.updateSelectedUserFollowingAndFollowers(selectedUser, new FollowCountObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
                }

                @Override
                public void handleSuccess(String followNum, String followerNum) {
                    view.setFollowingText(followNum);
                    view.setFollowerText(followerNum);
                }
            });
            setFollowingButton(false);
        }
    }

    private class MyFollowCountObserver implements FollowCountObserver {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(String followNum, String followerNum) {
            view.setFollowingText(followNum);
            view.setFollowerText(followerNum);
        }
    }
}
