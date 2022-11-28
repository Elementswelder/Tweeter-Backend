package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.byu.cs.tweeter.server.dao.pojobeans.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
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

    public boolean checkValidAuth(String authToken){
        try {
            DynamoDbTable<AuthTokenBean> authTokenTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));
            Key key = Key.builder().partitionValue(authToken).sortValue("@eyrdrdsdg").build();
            AuthTokenBean auth = authTokenTable.getItem(key);

            System.out.println("DEBUG - AUTHTOKEN DATE FROM TABLE: " + auth.getDate());
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            String dateNow = format.format(new Date().getTime());
            System.out.println("DEBUG - AUTHTOKEN DATE NOW: " + dateNow);

            //d1 is the originally logged in authtoken
            Date d1 = format.parse(auth.getDate());
            //d2 is the current time
            Date d2 = format.parse(dateNow);

            float floatyboi = ((d1.getTime() - d2.getTime()) / 1000) / 60;
            System.out.println("TIME AFTER MATH- MINUTES: " + floatyboi);
            if (floatyboi <= 5){
                return true;
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        //TODO: FIX THIS TO FALSE BEFORE TURNING IN
        return true;
    }

}
