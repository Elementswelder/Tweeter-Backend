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
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends KingDAO implements FollowDAOInterface {
    private int USER_NUM = 0;
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
                Key key = Key.builder().partitionValue(request.getCurrentUser().getAlias())
                        .sortValue(request.getFollowee().getAlias()).build();
                FollowsTableBean tableBean = table.getItem(key);
                if (tableBean == null){
                    System.out.println("false");
                    return new IsFollowerResponse(false);
                }
                else {
                    System.out.println("YES HE IS A FOLLOWER");
                    return new IsFollowerResponse(true);
                }
            } catch (NullPointerException ex){
                ex.printStackTrace();
                return new IsFollowerResponse(false);
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


    public Integer getFolloweeCount(User followee) {
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

    public void addFollowersBatch(List<User> users) {
        try {
            User userTarget = new User("Fred", "Boi", "@FreddyMan",
                    "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");
            List<FollowsTableBean> batchToWrite = new ArrayList<>();
            for (User u : users) {
                FollowsTableBean dto = new FollowsTableBean(u.getAlias(), u.getFirstName(), u.getLastName(),
                        u.getImageUrl(), userTarget.getAlias(), userTarget.getFirstName(), userTarget.getLastName(), userTarget.getImageUrl());
                batchToWrite.add(dto);

                if (batchToWrite.size() == 25) {
                    // package this batch up and send to DynamoDB.
                    writeChunkOfFollowTableBeans(batchToWrite);
                    batchToWrite = new ArrayList<>();
                }
            }

            // write any remaining
            if (batchToWrite.size() > 0) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFollowTableBeans(batchToWrite);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeChunkOfFollowTableBeans(List<FollowsTableBean> followTable) {
        try {
            if (followTable.size() > 25)
                throw new RuntimeException("Too many users to write");
            USER_NUM += followTable.size();
            DynamoDbTable<FollowsTableBean> table = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
            WriteBatch.Builder<FollowsTableBean> writeBuilder = WriteBatch.builder(FollowsTableBean.class).mappedTableResource(table);
            for (FollowsTableBean item : followTable) {
                writeBuilder.addPutItem(builder -> builder.item(item));
            }
            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBuilder.build()).build();

            try {
                BatchWriteResult result = getDbClient().batchWriteItem(batchWriteItemEnhancedRequest);

                // just hammer dynamodb again with anything that didn't get written this time
                if (result.unprocessedPutItemsForTable(table).size() > 0) {
                    System.out.println("ADDING CHUNK Size so far added: " + USER_NUM);
                    writeChunkOfFollowTableBeans(result.unprocessedPutItemsForTable(table));
                }

            } catch (DynamoDbException e) {
                System.out.println("FAILED TO FINISH FOLLOW TABLE");
                e.printStackTrace();
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
