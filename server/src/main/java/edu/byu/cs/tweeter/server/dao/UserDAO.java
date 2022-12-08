package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.response.FollowerCountResponse;
import edu.byu.cs.tweeter.response.FollowingCountResponse;
import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.RegisterResponse;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class UserDAO extends KingDAO implements UserDAOInterface {

    User user;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        try {
            System.out.println("Trying to get into dynamo tables");
            DynamoDbTable<UserTableBean> followsDynamoDbTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            UserTableBean user =  new UserTableBean(request.getUsername(), request.getFirstName(), request.getLastName(), request.getImage(), request.getPassword(), 0, 0);
            followsDynamoDbTable.putItem(user);

        } catch (Exception ex){
            ex.printStackTrace();
            return new RegisterResponse("FAILED TO ADD A USER TO THE TABLE");
        }
        user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), gets3Client().getUrl(s3BucketName, request.getUsername()).toString());
        //Create the DateTime for the AuthToken
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        return new RegisterResponse(user, new AuthToken(UUID.randomUUID().toString(), format.format(new Date())));
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        try {
            DynamoDbTable<UserTableBean> loginTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key key = Key.builder().partitionValue(request.getUsername()).build();
            UserTableBean user = loginTable.getItem(key);
            if (user != null){
                if (Hasher.validatePassword(request.getPassword(), user.getPassword())){
                    User newUser = new User(user.firstName, user.lastName, user.alias, gets3Client().getUrl(s3BucketName, request.getUsername()).toString());
                    SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    return new LoginResponse(newUser, new AuthToken(UUID.randomUUID().toString(), format.format(new Date())));
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new LoginResponse("FAILED TO FIND A MATCHING USER IN THE DATABASE");
        }
        return new LoginResponse("Failed to find a username/password match");
    }


    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        try {
            System.out.println("Inside Get User");
            DynamoDbTable<UserTableBean> loginTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key key = Key.builder().partitionValue(request.getAlias()).build();
            UserTableBean user = loginTable.getItem(key);
            if (user != null){
                System.out.println("User found - " + user.getAlias());
                User newUser = new User(user.firstName, user.lastName, user.alias, gets3Client().getUrl(s3BucketName, request.getAlias()).toString());
                return new GetUserResponse(newUser, request.getAuthToken());
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new GetUserResponse("FAILED TO FIND A MATCHING USER IN THE DATABASE");
        }
        return new GetUserResponse("FAILED TO GET USER - GENERIC getUser");
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        assert request.getUser() !=null;
        UserTableBean user = null;
        try {
            DynamoDbTable<UserTableBean> followingCount = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key key = Key.builder().partitionValue(request.getUser().getAlias()).build();
            user = followingCount.getItem(key);
            System.out.println(user.getAlias() + " has this many following: " + user.getFollowing());
            return new FollowingCountResponse(user.getFollowing(), request.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
            return new FollowingCountResponse("FAILED TO FIND A USER IN THE DATABASE - GET FOLLOWING COUNT");
        }
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        assert request.getUser() != null;
        UserTableBean user = null;
        try {
            DynamoDbTable<UserTableBean> followerCountTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key key = Key.builder().partitionValue(request.getUser().getAlias()).build();
            user = followerCountTable.getItem(key);
            System.out.println(user.getAlias() + " has this many followers: " + user.getFollowing());
            return new FollowerCountResponse(user.getFollowers(), request.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
            return new FollowerCountResponse("FAILED TO FIND A USER IN THE DATABASE - GET FOLLOWER COUNT");
        }
    }

    /**
     * The boolean is used to tell if the user is unfollowing or following someone. If it is true
     * add the count by 1, if it is false, lower the count by one
     * @param currentUser
     * @param followUser
     * @param following
     * @return
     */
    public boolean updateFollowCount(User currentUser, User followUser, boolean following){
        try {
            DynamoDbTable<UserTableBean> userTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key currentUserKey = Key.builder().partitionValue(currentUser.getAlias()).build();
            Key followUserKey = Key.builder().partitionValue(followUser.getAlias()).build();
            UserTableBean currentUserTable = userTable.getItem(currentUserKey);
            UserTableBean followUserTable = userTable.getItem(followUserKey);

            if (currentUserTable != null && followUserTable != null){
                if (following){
                    System.out.println("Adding " + followUser.getAlias() + " for : " + currentUser.getAlias());
                    currentUserTable.setFollowing(currentUserTable.getFollowing()+1);
                    followUserTable.setFollowers(followUserTable.getFollowers()+1);
                    userTable.updateItem(currentUserTable);
                    userTable.updateItem(followUserTable);
                    return true;
                }
                else {
                    System.out.println("Removing " + followUser.getAlias() + " from " + currentUser.getAlias());
                    currentUserTable.setFollowing(currentUserTable.getFollowing()-1);
                    followUserTable.setFollowers(followUserTable.getFollowers()-1);
                    userTable.updateItem(currentUserTable);
                    userTable.updateItem(followUserTable);
                    return true;
                }

            } else {
                return false;
            }

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void addUserBatch(List<User> users) {
        try {
            List<UserTableBean> batchToWrite = new ArrayList<>();
            for (User u : users) {
                UserTableBean dto = new UserTableBean(u);
                batchToWrite.add(dto);

                if (batchToWrite.size() == 25) {
                    // package this batch up and send to DynamoDB.
                    writeChunkOfUserTableBeans(batchToWrite);
                    batchToWrite = new ArrayList<>();
                }
            }

            // write any remaining
            if (batchToWrite.size() > 0) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserTableBeans(batchToWrite);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeChunkOfUserTableBeans(List<UserTableBean> userDTOs) {
        try {
            if (userDTOs.size() > 25)
                throw new RuntimeException("Too many users to write");

            DynamoDbTable<UserTableBean> table = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            WriteBatch.Builder<UserTableBean> writeBuilder = WriteBatch.builder(UserTableBean.class).mappedTableResource(table);
            for (UserTableBean item : userDTOs) {
                writeBuilder.addPutItem(builder -> builder.item(item));
            }
            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBuilder.build()).build();

            try {
                BatchWriteResult result = getDbClient().batchWriteItem(batchWriteItemEnhancedRequest);

                // just hammer dynamodb again with anything that didn't get written this time
                if (result.unprocessedPutItemsForTable(table).size() > 0) {
                    writeChunkOfUserTableBeans(result.unprocessedPutItemsForTable(table));
                }

            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
