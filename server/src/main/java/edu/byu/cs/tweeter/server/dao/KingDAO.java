package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class KingDAO {

    protected AmazonS3 s3client;
    protected final String s3BucketName = "photo-bucket-cs340";

    protected DynamoDbClient dbClient;
    protected DynamoDbEnhancedClient enhancedClient;

    protected AmazonS3 gets3Client(){
        if (s3client == null){
            s3client = AmazonS3ClientBuilder
                    .standard()
                    .withRegion("us-east-1")
                    .build();
        }
        return s3client;
    }

    protected DynamoDbEnhancedClient getDbClient(){
        if (dbClient == null){
            dbClient = DynamoDbClient.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(
                            //Please don't steal them, thx
                            AwsBasicCredentials.create("AKIASWZRQ2UG4NR5DWMA", "lKHSN+WX0+LN1ep3hbGA/aK/l8mJPF3gcavA4kOA")
                    ))
                    .region(Region.US_EAST_1)
                    .build();

            enhancedClient = DynamoDbEnhancedClient
                    .builder()
                    .dynamoDbClient(dbClient)
                    .build();
        }
        return enhancedClient;
    }

}
