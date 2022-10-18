package edu.byu.cs.tweeter.client.service;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.LoginsHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.LoginsObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;

public class UserService extends ServiceHandler<Runnable>{


    public void login(String username, String password, LoginsObserver observer){
        LoginTask loginTask = new LoginTask(username, password, new LoginsHandler(observer));
        startTask(loginTask);
    }

    public void register(String firstName, String lastName, String username, String password, String image, LoginsObserver observer){
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username,
                password, image, new LoginsHandler(observer));
        startTask(registerTask);
    }

    public void logout(SimpleNotifyObserver observer){
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleNotificationHandler(observer));
        startTask(logoutTask);
    }
}
