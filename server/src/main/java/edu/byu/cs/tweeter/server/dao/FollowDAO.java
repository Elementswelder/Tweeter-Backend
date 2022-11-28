package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.response.FollowResponse;
import edu.byu.cs.tweeter.response.FollowerCountResponse;
import edu.byu.cs.tweeter.response.FollowerResponse;
import edu.byu.cs.tweeter.response.FollowingCountResponse;
import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.response.UnfollowResponse;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.response.FollowingResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends KingDAO implements FollowDAOInterface {

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @return said count.
     */

    public IsFollowerResponse isFollower(IsFollowerRequest request){
        assert request.getFollower() != null;
        assert request.getFollowee() != null;
        return new IsFollowerResponse(new Random().nextInt() > 0, request.getAuthToken());
    }

    public FollowResponse followUser(FollowRequest request){
        return new FollowResponse(request.getAuthToken());
    }

    public UnfollowResponse unFollowUser(UnfollowRequest request){
        return new UnfollowResponse(request.getAuthToken());
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
    public FollowingResponse getFollowees(FollowingRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    public FollowerResponse getFollowers(FollowersRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();

            }
        }

        return new FollowerResponse(responseFollowees, hasMorePages);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        assert request.getUser() !=null;
        return new FollowingCountResponse(getFollowerCountNum(request.getUser()), request.getAuthToken());
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request){
        assert request.getUser() !=null;
        if (!checkValidAuth(request.getAuthToken().getToken())){
            return new FollowerCountResponse("AuthToken Expired, please log in again");
        }

        int followeeCount = getFolloweeCount(request.getUser());
        System.out.println("follow count debug - " + followeeCount);
        if (followeeCount == -1){
            return new FollowerCountResponse("COULD NOT FIND THE USER IN THE DATABASE");
        }
        return new FollowerCountResponse(followeeCount, request.getAuthToken());
    }

    public Integer getFolloweeCount(User follower) {
        assert follower != null;
        try {
            DynamoDbTable<org.example.FollowsTableBean> followeeCountTable = getDbClient().table("follows", TableSchema.fromBean(org.example.FollowsTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(follower.getAlias())
                            .build()))
                    .scanIndexForward(true);

            QueryEnhancedRequest request = requestBuilder.build();

            List<org.example.FollowsTableBean> result = followeeCountTable.query(request)
                    .items()
                    .stream()
                    .collect(Collectors.toList());

            System.out.println(result.size());

            return result.size();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    public Integer getFollowerCountNum(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        try {
            DynamoDbIndex<org.example.FollowsTableBean> followerCountTable = getDbClient().table("follows", TableSchema.fromBean(org.example.FollowsTableBean.class)).index("followee_handle-follower_handle-index");
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .build()));


            Iterator<org.example.FollowsTableBean> feedResults = (Iterator<org.example.FollowsTableBean>) followerCountTable.query(requestBuilder);

            System.out.println(result.size());

            return result.size();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
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
