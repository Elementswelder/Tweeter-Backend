package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.security.auth.kerberos.KerberosTicket;

import edu.byu.cs.tweeter.model.domain.AuthToken;
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
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
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
            System.out.println("In isFollower");
            System.out.println("Follower: " + request.getCurrentUser().getAlias() + " Followee: " + request.getFollowee().getAlias());
            DynamoDbTable<FollowsTableBean> table = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
            try {
                Key key = Key.builder().partitionValue(request.getFollowee().getAlias())
                        .sortValue(request.getCurrentUser().getAlias()).build();
                FollowsTableBean tableBean = table.getItem(key);
                if (tableBean == null){
                    System.out.println("false");
                    return new IsFollowerResponse(false, request.getAuthToken());
                }
                else {
                    System.out.println("YES HE IS A FOLLOWER");
                    return new IsFollowerResponse(true, request.getAuthToken());
                }
            } catch (NullPointerException ex){
                ex.printStackTrace();
                return new IsFollowerResponse(false, request.getAuthToken());
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new IsFollowerResponse("FAILED TO CONNECT TO DYNAMODB - isFollower - FOLLOWDAO");
        }
    }

    public FollowResponse followUser(FollowRequest request){
        System.out.println("inside follow user");
        DynamoDbTable<FollowsTableBean> table = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
        try {
            FollowsTableBean addFollow = new FollowsTableBean(request.getCurrentUser().getAlias(), request.getCurrentUser().getFirstName(),
                    request.getCurrentUser().getLastName(), request.getCurrentUser().getImageUrl(), request.getFollowee().getAlias(),
                    request.getFollowee().getFirstName(), request.getFollowee().getLastName(), request.getFollowee().getImageUrl());
            table.putItem(addFollow);
        } catch (Exception ex){
            ex.printStackTrace();
            return new FollowResponse("UNABLE TO ADD A NEW USER TO THE DATABASE");
        }
        return new FollowResponse(request.getAuthToken());
    }

    public UnfollowResponse unFollowUser(UnfollowRequest request){
        System.out.println("inside unfollow user");
        DynamoDbTable<FollowsTableBean> table = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
        try {
            Key key = Key.builder().partitionValue(request.getCurrentUser().getAlias())
                    .sortValue(request.getFollowee().getAlias()).build();
            table.deleteItem(key);
        } catch (Exception ex){
            ex.printStackTrace();
            return new UnfollowResponse("UNABLE TO REMOVE A NEW USER TO THE DATABASE");
        }
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
    public Pair<List<User>, Boolean> getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        try {
            DynamoDbTable<FollowsTableBean> table = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
            Key key = Key.builder()
                    .partitionValue(request.getFollowerAlias())
                    .build();
            System.out.println("GET FOLLOWEES 1 out of 3 ");
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key));

           /* if(isNotEmptyString(request.getLastFolloweeAlias())) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put("followee_handle", AttributeValue.builder().s(request.getLastFolloweeAlias()).build());

                requestBuilder.exclusiveStartKey(startKey);
            } */
            System.out.println("2 out of 3 ");
            QueryEnhancedRequest queryRequest = requestBuilder.build();
            List<FollowsTableBean> allFollowees = table.query(queryRequest).items().stream().limit(request.getLimit()).collect(Collectors.toList());
            List<User> followerUser = new ArrayList<>();

            for (FollowsTableBean follow : allFollowees) {
                User user = new User(follow.getFollowing_first_name(), follow.getFollowing_last_name(),
                        follow.getFollowee_handle(), follow.getFollowing_image());
                followerUser.add(user);
            }
            System.out.println("3 out of 3 " + allFollowees.size());
            boolean hasMorePages = allFollowees.size() == request.getLimit();
            return new Pair<>(followerUser, hasMorePages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pair<List<User>, Boolean> getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        try {
            DynamoDbIndex<FollowsTableBean> index = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class)).index("followIndex");
            Key key = Key.builder()
                    .partitionValue(request.getFollowerAlias())
                    .build();
            System.out.println("GET FOLLOWERS 1 out of 3 ");
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).limit(request.getLimit());


           /* if(isNotEmptyString(request.getLastFolloweeAlias())) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("followee_handle", AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put("follower_handle", AttributeValue.builder().s(request.getLastFolloweeAlias()).build());

                requestBuilder.exclusiveStartKey(startKey);
            } */
            System.out.println("2 out of 3 ");
            List<FollowsTableBean> allFollowers = new ArrayList<>();
            QueryEnhancedRequest queryRequest = requestBuilder.build();
            SdkIterable<Page<FollowsTableBean>> result = index.query(queryRequest);
            PageIterable<FollowsTableBean> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followDTOPage -> followDTOPage.items().forEach(v -> allFollowers.add(v)));
            List<User> followerAliases = new ArrayList<>();
            for (FollowsTableBean follow : allFollowers) {
                User user = new User(follow.getFollower_first_name(), follow.getFollower_last_name(),
                        follow.getFollower_handle(), follow.getFollower_image());
                followerAliases.add(user);
            }
            System.out.println("3 out of 3 " + followerAliases.size());
            boolean hasMorePages = allFollowers.size() == request.getLimit();

            return new Pair<>(followerAliases, hasMorePages);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean isNotEmptyString(String lastFolloweeAlias) {
        return (lastFolloweeAlias.length() > 0 && lastFolloweeAlias != null);
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
