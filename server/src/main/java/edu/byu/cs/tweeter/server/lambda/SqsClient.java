package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SqsClient {
    private static final String postStatusQueue = "https://sqs.us-east-1.amazonaws.com/186398070029/postStatusQueue";
    private static final String updateFeedQueue = "https://sqs.us-east-1.amazonaws.com/186398070029/updateFeedQueue";

    public static void sendMessage(String url, String body) {
        System.out.println("INSIDE SEND MESSAGE SQSCLIENT");
        System.out.println(body);
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(url)
                .withMessageBody(body);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);
    }

    public static String getPostStatusQueueUrl() {
        return postStatusQueue;
    }

    public static String getUpdateFeedQueueUrl() {
        return updateFeedQueue;
    }
}