package edu.byu.cs.tweeter.server.dao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.response.PostStatusResponse;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.StatusDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.StoryTableBean;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StoryDAO extends KingDAO implements StatusDAOInterface {

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    public Integer getStatusesCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        return getStatusesCount(follower);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public Pair<List<Status>, Boolean> getStatuses(StatusRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatusString() != null;

        try {
            DynamoDbTable<StoryTableBean> storyTable = getDbClient().table("StoryTable", TableSchema.fromBean(StoryTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias())
                            .build()))
                    .scanIndexForward(false);

            if(isNotEmptyString(request.getLastStatusString())){
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("user_alias", AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put("time_stamp", AttributeValue.builder().s(request.getLastStatusString()).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

            System.out.println("made it to the list");
            List<StoryTableBean> allStories = storyTable.query(queryEnhancedRequest)
                    .items()
                    .stream()
                    .limit(request.getLimit())
                    .collect(Collectors.toList());

            // List<Status> alLStatus = getFakeData().getFakeStatuses();
            List<Status> responseStatuses = new ArrayList<>(request.getLimit());

            for (StoryTableBean story : allStories){
                Date date = new Date(story.getTimestamp());
                responseStatuses.add(new Status(story.getPost(), date.toString(), story.getUrls(), story.getMentions()));

            }
            boolean hasMorePages = responseStatuses.size() == request.getLimit();
            return new Pair<>(responseStatuses, hasMorePages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isNotEmptyString(String s) {
        return (s != null && s.length() > 0);
    }


    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatusString() != null;

        List<Status> alLStatus = getFakeData().getFakeStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (alLStatus != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastStatusString(), alLStatus);

                for(int limitCounter = 0; followeesIndex < alLStatus.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseStatuses.add(alLStatus.get(followeesIndex));
                }

                hasMorePages = followeesIndex < alLStatus.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        assert request.getStatus() != null;
        System.out.println("Trying to post a status");
        try {
            DynamoDbTable<StoryTableBean> storyTable = getDbClient().table("StoryTable", TableSchema.fromBean(StoryTableBean.class));
            StoryTableBean newPost = new StoryTableBean(request.getStatus().getPost(), DateTime.now().getMillis(),
                    request.getStatus().urls, request.getStatus().mentions, request.getStatus().getUser().getAlias());

            storyTable.putItem(newPost);
        } catch (Exception e) {
            e.printStackTrace();
            return new PostStatusResponse("FAILED TO ADD TO TABLES - POSTSTATUS DAO");
        }

        return new PostStatusResponse(request.getAuthToken());
    }

    /**
     * Determines the index for the first followee in the specified 'allStatuses' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allStatuses the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<Status> allStatuses) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastFolloweeAlias.equals(allStatuses.get(i).getDate())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getStatusStartingIndex(String lastStatusDate, String lastUserAlias, List<StoryTableBean> allStatuses) {

        int followeesIndex = 0;

        if(lastStatusDate != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatusDate.equals(allStatuses.get(i).getPost()) && lastUserAlias.equals(allStatuses.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
