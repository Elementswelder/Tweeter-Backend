package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.security.auth.kerberos.KerberosTicket;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.response.FollowResponse;
import edu.byu.cs.tweeter.response.FollowerResponse;
import edu.byu.cs.tweeter.response.IsFollowerResponse;
import edu.byu.cs.tweeter.response.UnfollowResponse;
import edu.byu.cs.tweeter.request.FollowRequest;
import edu.byu.cs.tweeter.request.FollowersRequest;
import edu.byu.cs.tweeter.request.FollowingRequest;
import edu.byu.cs.tweeter.response.FollowingResponse;
import edu.byu.cs.tweeter.request.IsFollowerRequest;
import edu.byu.cs.tweeter.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.FollowsTableBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
        assert request.getCurrentUser() != null;
        assert request.getFollowee() != null;
        try {
            DynamoDbTable<FollowsTableBean> table = enhancedClient.table("follows", TableSchema.fromBean(FollowsTableBean.class));
            Key key = Key.builder().partitionValue(request.getCurrentUser().getAlias())
                    .sortValue(request.getFollowee().getAlias()).build();
            FollowsTableBean tableBean = table.getItem(key);

            if (tableBean == null){
                return new IsFollowerResponse(false, request.getAuthToken());
            }
            else {
                return new IsFollowerResponse(true, request.getAuthToken());
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new IsFollowerResponse("FAILED TO CONNECT TO DYNAMODB - isFollower - FOLLOWDAO");
        }
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
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;
        try {

            DynamoDbTable<FollowsTableBean> followeeCountTable = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias())
                            .build()))
                    .scanIndexForward(true);

            QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

            System.out.println("made it to the list");
            List<FollowsTableBean> allFollowees = followeeCountTable.query(queryEnhancedRequest)
                    .items()
                    .stream()
                    .collect(Collectors.toList());
            // List<User> allFollowees = getDummyFollowees();
            List<User> responseFollowees = new ArrayList<>(request.getLimit());

            boolean hasMorePages = false;

            if (request.getLimit() > 0) {
                if (allFollowees != null) {
                    int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                    for (int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                        User user = new User(allFollowees.get(followeesIndex).getFollower_first_name(),
                                allFollowees.get(followeesIndex).getFollower_last_name(), allFollowees.get(followeesIndex).getFollower_handle(),
                                allFollowees.get(followeesIndex).getFollower_image());
                        responseFollowees.add(user);
                    }

                    hasMorePages = followeesIndex < allFollowees.size();
                }
            }
            return new FollowingResponse(responseFollowees, hasMorePages);
        } catch (Exception ex){
            ex.printStackTrace();
            return new FollowingResponse("FAILED TO GET THE FOLLOWEES - FOLLOWDAO");
        }
    }

    public FollowerResponse getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        try {
            DynamoDbIndex<FollowsTableBean> followerCountTable = getDbClient()
                    .table("follows", TableSchema.fromBean(FollowsTableBean.class))
                    .index("followIndex");

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias())
                            .build()))
                    .scanIndexForward(true);

            if (isNotEmptyString(request.getLastFolloweeAlias())){
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follow_index", AttributeValue.builder().s(request.))
            }

            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(request.getFollowerAlias())
                    .build());
            System.out.println("made it to the list for iterator");
            Iterator<Page<FollowsTableBean>> result = followerCountTable.query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build()).iterator();
            Page<FollowsTableBean> list = result.next();


            List<FollowsTableBean> allFollowees = list.items();
            List<User> responseFollowees = new ArrayList<>(request.getLimit());

            boolean hasMorePages = false;

            if (request.getLimit() > 0) {
                if (allFollowees != null) {
                    int followeesIndex = getFollowersStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                    for (int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                        User user = new User(allFollowees.get(followeesIndex).getFollowing_first_name(),
                                allFollowees.get(followeesIndex).getFollowing_last_name(), allFollowees.get(followeesIndex).getFollowee_handle(),
                                allFollowees.get(followeesIndex).getFollowing_image());
                        responseFollowees.add(user);
                    }

                    hasMorePages = followeesIndex < allFollowees.size();

                }
            }
            return new FollowerResponse(responseFollowees, hasMorePages);
        } catch (Exception ex){
            ex.printStackTrace();
            return new FollowerResponse("FAILED TO GET FOLLOWERS - FOLLOW DAO");
        }
    }


    /*public Integer getFolloweeCount(User followee) {
        assert followee != null;
        try {
            System.out.println("Inside Followee Count");
            DynamoDbTable<FollowsTableBean> followeeCountTable = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(followee.getAlias())
                            .build()))
                    .scanIndexForward(true);

            QueryEnhancedRequest request = requestBuilder.build();

            System.out.println("made it to the list");
            List<FollowsTableBean> result = followeeCountTable.query(request)
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
        assert follower != null;
        try {
           DynamoDbIndex<FollowsTableBean> followerCountTable = getDbClient()
                   .table("follows", TableSchema.fromBean(FollowsTableBean.class))
                   .index("followIndex");

           QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                   .partitionValue(follower.getAlias())
                   .build());
            System.out.println("made it to the list for iterator");
           Iterator<Page<FollowsTableBean>> result = followerCountTable.query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build()).iterator();
           Page<FollowsTableBean> list = result.next();
           System.out.println("dset");
            return list.items().size();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    } */

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
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<FollowsTableBean> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getFollowee_handle())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getFollowersStartingIndex(String lastFolloweeAlias, List<FollowsTableBean> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getFollower_handle())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }
}
