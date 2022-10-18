package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.backgroundTask.observer.LoginsObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginsPresenter extends Presenter{

    protected LoginsView view;


    public LoginsPresenter(LoginsView view) {
        super(view);
        this.view = view;
    }

    public interface LoginsView extends View {
        void clearMessage();
        void setIntent(User user);
        void displayErrorMessage(String message);
    }

    protected boolean clearAndDisplayLogin(String message, String whichLogin){
        if (message == null) {
            view.displayMessage(whichLogin);
            return true;
        }
        else {
            view.clearMessage();
            view.displayErrorMessage(message);
            return false;
        }
    }


    protected class LoginRegisterObserver implements LoginsObserver {

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            view.displayMessage("Hello " + user.getFirstName());
            view.clearMessage();
            view.setIntent(user);
        }

        @Override
        public void handleFailure(String message){
            view.clearMessage();
            view.displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.clearMessage();
            view.displayErrorMessage(exception.getMessage());
        }
    }
}
