package edu.byu.cs.tweeter.client;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.service.StatusService;
import edu.byu.cs.tweeter.client.cache.service.UserService;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.LoginsPresenter;
import edu.byu.cs.tweeter.client.presenter.Presenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.net.TweeterRemoteException;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.response.StatusResponse;

public class PostStatusTest {

    public static final String URL_LOGIN = "/login";
    public static final String URL_POST = "/poststatus";
    public static final String URL_GET_STORY = "/getstory";
    private final AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), DateTime.now().toString());
    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private LoginRequest loginRequest = new LoginRequest("@man", "123");


    private User fakeUser;
    private AuthToken fakeAuthToken;
    private Presenter.View mainViewMock;
    private LoginPresenter loginPresenterSpy;
    private ServerFacade serverFacade;
    private StatusService userServiceMock;
    private CountDownLatch countDownLatch;
    public StoryObserverTest observer;
    StatusService statusServiceSpy;

    private class StoryObserverTest implements PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> story;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.story = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.story = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            mainViewMock.displayMessage("Successfully Posted!");
            this.message = null;
            this.story = new ArrayList<>();
            this.hasMorePages = true;
            this.exception = null;
            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStory() {
            return story;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    @BeforeEach
    void setUp() {
        serverFacade = new ServerFacade();
        mainViewMock = Mockito.mock(LoginsPresenter.LoginsView.class);
        LoginPresenter loginPresenter = new LoginPresenter((LoginsPresenter.LoginsView) mainViewMock);
        statusServiceSpy = Mockito.spy(new StatusService());
        loginPresenterSpy = Mockito.spy(loginPresenter);
        userServiceMock = Mockito.mock(StatusService.class);

        Mockito.doReturn(userServiceMock).when(loginPresenterSpy).getStatusService();
        resetCountDownLatch();
    }

    @Test
    public void testPostStatus() throws IOException, TweeterRemoteException, InterruptedException {
        LoginResponse loginResponse = serverFacade.login(loginRequest, URL_LOGIN);

        Assertions.assertTrue(loginResponse.isSuccess());
        Status status = new Status("new post", loginResponse.getUser(), "2022", null, null);
        PostStatusRequest postStatusRequest = new PostStatusRequest(authToken, status);
        PostStatusResponse postStatusResponse = serverFacade.postStatus(postStatusRequest, URL_POST);
        observer = new StoryObserverTest();
        statusServiceSpy.loadMoreStatus(loginResponse.getAuthToken(), loginResponse.getUser(), 10, status, observer);
        awaitCountDownLatch();

        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Mockito.verify(mainViewMock).displayMessage("Successfully Posted!");

        StatusRequest getStoryRequest = new StatusRequest(loginResponse.getAuthToken(), status.getUser().getAlias(), 10, null);
        StatusResponse getStoryResponse = serverFacade.getStatus(getStoryRequest, URL_GET_STORY);

        Assertions.assertEquals(getStoryResponse.getStatuses().get(0).getPost(), status.getPost());



    }
}