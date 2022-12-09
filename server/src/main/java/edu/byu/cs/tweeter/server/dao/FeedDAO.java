package edu.byu.cs.tweeter.server.dao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.FeedTableBean;
import edu.byu.cs.tweeter.server.dao.pojobeans.FollowsTableBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDAO extends KingDAO implements FeedDAOInterface {
    @Override
    public Pair<List<Status>, Boolean> getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatusString() != null;

        try {
            System.out.println("Inside GetFeed");
            System.out.println("User is " + request.getFollowerAlias());
            DynamoDbTable<FeedTableBean> feedTable = getDbClient().table("FeedTable", TableSchema.fromBean(FeedTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias())
                            .build()))
                    .scanIndexForward(false);
            /*
            if(isNotEmptyString(request.getLastStatusTime())){
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("user_alias", AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put("time_stamp", AttributeValue.builder().s(request.lastStatus().getDate()).build());

                requestBuilder.exclusiveStartKey(startKey);
            } */
            System.out.println("1 out of 3");
            QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

            System.out.println("made it to the list");
            List<FeedTableBean> allStories = feedTable.query(queryEnhancedRequest)
                    .items()
                    .stream()
                    .limit(request.getLimit())
                    .collect(Collectors.toList());
            System.out.println("2 out of 3");
            // List<Status> alLStatus = getFakeData().getFakeStatuses();
            List<Status> responseStatuses = new ArrayList<>(request.getLimit());

            for (FeedTableBean story : allStories){
                // Date date = new Date(story.getTime_stamp());
                System.out.println("adding story");
                User newUser = new User("", "" ,story.getUser_alias(), "");
                responseStatuses.add(new Status(story.getPost(), newUser, DateTime.now().toString(), story.getUrls(), story.getMentions()));
                System.out.println("adding story done");
            }
            boolean hasMorePages = responseStatuses.size() == request.getLimit();
            System.out.println("3 out of 3 " + allStories.size());
            return new Pair<>(responseStatuses, hasMorePages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }




    }

    @Override
    public boolean addFeedItem(Status status, List<User> followers) {
        System.out.println("Inside addFeedItem");
        try {
            DynamoDbTable<FeedTableBean> feedTable = getDbClient().table("FeedTable", TableSchema.fromBean(FeedTableBean.class));

            for (int i = 0; i < followers.size(); i++){
                FeedTableBean feedPost = new FeedTableBean(status.getPost(), status.getDatetime(), status.getUrls(),
                        status.getMentions(), status.getUser().getAlias(), followers.get(i).getAlias());
                System.out.println("adding story num: " + i);
                feedTable.putItem(feedPost);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void writeChunk(List<FeedTableBean> followTable) {
        try {
            if (followTable.size() > 25)
                throw new RuntimeException("Too many feed to write");
            DynamoDbTable<FeedTableBean> table = getDbClient().table("FeedTable", TableSchema.fromBean(FeedTableBean.class));
            WriteBatch.Builder<FeedTableBean> writeBuilder = WriteBatch.builder(FeedTableBean.class).mappedTableResource(table);
            for (FeedTableBean item : followTable) {
                writeBuilder.addPutItem(builder -> builder.item(item));
            }
            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBuilder.build()).build();

            try {
                BatchWriteResult result = getDbClient().batchWriteItem(batchWriteItemEnhancedRequest);

                if (result.unprocessedPutItemsForTable(table).size() > 0) {
                    System.out.println("feed ADDING CHUNK Size so far added: ");
                    writeChunk(result.unprocessedPutItemsForTable(table));
                }

            } catch (DynamoDbException e) {
                System.out.println("FAILED TO FINISH FEED TABLE");
                e.printStackTrace();
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
