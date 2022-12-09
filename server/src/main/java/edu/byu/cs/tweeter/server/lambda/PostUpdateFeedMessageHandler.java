package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.FeedList;
import edu.byu.cs.tweeter.server.dao.KingDAO;
import edu.byu.cs.tweeter.server.dao.pojobeans.FeedTableBean;
import edu.byu.cs.tweeter.server.dao.pojobeans.FollowsTableBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class PostUpdateFeedMessageHandler extends KingDAO implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        try {
            System.out.println("INSIDE POST UPDATEFEED HANDLER");
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                Status status = null;
                try {
                    status = new Gson().fromJson(msg.getBody(), Status.class);
                } catch (JsonSyntaxException | IllegalStateException e) {
                    e.printStackTrace();
                }
                System.out.println("Asserting that the status is not null");
                assert status != null;
                String authorAlias = status.getUser().getAlias();
                DynamoDbTable<FollowsTableBean> followsTable = getDbClient().table("follows", TableSchema.fromBean(FollowsTableBean.class));
                Iterator<FollowsTableBean> results = followsTable.scan().items().iterator();
                List<FeedTableBean> feedTableList = new ArrayList<>();
                int i = 0;
                int batchSize = 500;
                while (results.hasNext()) {
                    String receiverAlias = results.next().getFollower_handle();
                    FeedTableBean feedTableItem = new FeedTableBean(status.getPost(), DateTime.now().toString(),
                            status.getUrls(), status.getMentions(), authorAlias, receiverAlias);
                    feedTableList.add(feedTableItem);
                    ++i;
                    if (i % batchSize == 0 && i != 0) {
                        FeedList feedList = new FeedList(feedTableList);
                        String body = new Gson().toJson(feedList);
                        SqsClient.sendMessage(SqsClient.getUpdateFeedQueueUrl(), body);
                        feedTableList.clear();
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
