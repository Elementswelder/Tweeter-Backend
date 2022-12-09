package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.FeedList;
import edu.byu.cs.tweeter.server.dao.pojobeans.FeedTableBean;
import edu.byu.cs.tweeter.server.service.StatusService;

public class AddFeedMesagesHandler extends KingHandler implements RequestHandler<SQSEvent, Void> {
        @Override
        public Void handleRequest(SQSEvent event, Context context) {
            StatusService statusService = new StatusService(getFactoryInterface());
            for (SQSEvent.SQSMessage msg : event.getRecords()) {

                System.out.println("UpdateFeeds BODY: " + msg.getBody());
                FeedList feedDTOList = new Gson().fromJson(msg.getBody(), FeedList.class);
                List<FeedTableBean> feedDTOS = feedDTOList.getFeedList();
                statusService.addFeedBatch(feedDTOS);
            }
            return null;
        }
}
