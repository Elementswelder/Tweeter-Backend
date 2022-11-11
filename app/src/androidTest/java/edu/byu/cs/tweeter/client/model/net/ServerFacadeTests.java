package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.request.RegisterRequest;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */

public class ServerFacadeTests {


    private ServerFacade serverFacade;
    private CountDownLatch countDownLatch;
    private AuthToken authToken;
    private String URL_REGISTER = "/register";
    private String URL_FOLLOWERS = "/getfollowers";
    private String URL_FOLLOWING_COUNT = "/getfollowingcount";
    private String URL_FOLLOWER_COUNT = "/getfollowerscount";


    @BeforeEach
    public void setup() {
        resetCountDownLatch();
        serverFacade = new ServerFacade();
        authToken = new AuthToken("auth", "time");
    }

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest("firstname", "lastname",
                "image", "@allen", "password(very secure)");
        RegisterResponse response;

        try {
            response = serverFacade.register(request, URL_REGISTER);
           // awaitCountDownLatch();
            assert response.isSuccess();
            assert response.getUser() != null;
            assert Objects.equals(response.getUser().getAlias(), "@allen");


        } catch (IOException | TweeterRemoteException ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetFollowers() {
        FollowersRequest request = new FollowersRequest(authToken, "@allen", 10, "test");
        FollowerResponse response;

        try {
            response = serverFacade.getFollowers(request, URL_FOLLOWERS);
            // awaitCountDownLatch();
            assert response.isSuccess();
            assert response.getFollowees().size() == 10;
            assert response.getHasMorePages();


        } catch (IOException | TweeterRemoteException ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetFollowingCount() {
        User user = new User("first", "last", "@allen", "image");
        FollowingCountRequest request = new FollowingCountRequest(authToken, user);
        FollowingCountResponse response;

        try {
            response = serverFacade.getFollowingCount(request, URL_FOLLOWING_COUNT);
            // awaitCountDownLatch();
            assert response.isSuccess();
            assert response.getCount() == 21;


        } catch (IOException | TweeterRemoteException ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetFollowersCount() {
        User user = new User("first", "last", "@allen", "image");
        FollowerCountRequest request = new FollowerCountRequest(authToken, user);
        FollowerCountResponse response;

        try {
            response = serverFacade.getFollowerCount(request, URL_FOLLOWER_COUNT);
            // awaitCountDownLatch();
            assert response.isSuccess();
            assert response.getCount() == 21;


        } catch (IOException | TweeterRemoteException ex){
            ex.printStackTrace();
        }
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }
}