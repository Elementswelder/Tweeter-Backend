package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.backgroundTask.Handlers.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.service.StatusService;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {

 /*   private User currentUser;
    private Status newStatus;
    private AuthToken currentAuthToken;

    private StatusService followServiceSpy;
    private PagedObserver<Status> observer = new FollowServiceObserver();

    private CountDownLatch countDownLatch;


    @BeforeEach
    public void setup() {
        List<String> urls = new ArrayList<>();
        List<String> mention =new ArrayList<>();
        currentUser = new User("FirstName", "LastName", null);
        newStatus = new Status("post", currentUser, "datetime", urls, mention);
        currentAuthToken = new AuthToken();

        followServiceSpy = Mockito.spy(new StatusService());

        // Setup an observer for the FollowService
        observer = new FollowServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }


    private class FollowServiceObserver implements PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> followees;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> followees, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.followees = followees;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.followees = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.followees = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getFollowees() {
            return followees;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    @Test
    public void testGetFollowees_validRequest_correctResponse() throws InterruptedException {
        followServiceSpy.loadMoreStatus(currentAuthToken, currentUser, 3, null, observer);
        awaitCountDownLatch();

        List<Status> expectedFollowees = FakeData.getInstance().getFakeStatuses().subList(0, 3);
        Assertions.assertTrue(observer.isSuccess);
        Assertions.assertNull(observer.getMessage());
        Assertions.assertEquals(expectedFollowees, observer.getFollowees());
        Assertions.assertTrue(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }


    @Test
    public void testGetFollowees_validRequest_loadsProfileImages() throws InterruptedException {
        followServiceSpy.loadMoreStatus(currentAuthToken, currentUser, 3, null, observer);
        awaitCountDownLatch();

        List<Status> followees = observer.getFollowees();
        Assertions.assertTrue(followees.size() > 0);
    }

    @Test
    public void testGetFollowees_invalidRequest_returnsNoFollowees() throws InterruptedException {
        followServiceSpy.loadMoreStatus(null, null, 0, null, observer);
        awaitCountDownLatch();

        Assertions.assertFalse(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertNull(observer.getFollowees());
        Assertions.assertFalse(observer.getHasMorePages());
        Assertions.assertNotNull(observer.getException());
    }
    */

}