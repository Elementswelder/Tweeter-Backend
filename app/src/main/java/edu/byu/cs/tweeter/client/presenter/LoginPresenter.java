package edu.byu.cs.tweeter.client.presenter;

public class LoginPresenter extends LoginsPresenter {


    public LoginPresenter(LoginsView view) {
        super(view);
    }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password); // This should go to the presenter
        if (clearAndDisplayLogin(message, "Logging in")) {
            userService.login(username, password, new LoginRegisterObserver());
        }
    }

    public String validateLogin(String username, String password) {
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }
}
