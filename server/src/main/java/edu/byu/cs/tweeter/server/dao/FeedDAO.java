package edu.byu.cs.tweeter.server.dao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.request.FeedRequest;
import edu.byu.cs.tweeter.request.StatusRequest;
import edu.byu.cs.tweeter.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.interfaces.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.FeedTableBean;
import edu.byu.cs.tweeter.server.dao.pojobeans.StoryTableBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public class FeedDAO extends KingDAO implements FeedDAOInterface {
    @Override
    public Pair<List<Status>, Boolean> getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatusString() != null;

        try {
            DynamoDbTable<FeedTableBean> feedTable = getDbClient().table("FeedTable", TableSchema.fromBean(FeedTableBean.class));

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias())
                            .build()))
                    .scanIndexForward(false);

           /* if(isNotEmptyString(request.getLastStatusTime())){
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
                responseStatuses.add(new Status(story.getPost(), DateTime.now().toString(), story.getUrls(), story.getMentions()));
                System.out.println("adding story done");
            }
            boolean hasMorePages = responseStatuses.size() == request.getLimit();
            System.out.println("3 out of 3 " + allStories.size());
            System.out.println(responseStatuses.get(0).post);
            return new Pair<>(responseStatuses, hasMorePages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
