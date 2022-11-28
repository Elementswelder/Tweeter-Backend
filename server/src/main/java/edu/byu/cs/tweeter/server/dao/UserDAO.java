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
import java.util.Date;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.request.GetUserRequest;
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


public class UserDAO extends KingDAO implements UserDAOInterface {

    User user;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        System.out.println("Trying to upload to S3 Bucket");
        try {
            //Convert the image and push it as a public image
            InputStream target = new ByteArrayInputStream(request.getImage().getBytes(StandardCharsets.UTF_8));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(request.getImage().length());
            metadata.setContentType("image/jpeg");
            gets3Client().putObject(new PutObjectRequest(s3BucketName, request.getUsername(), target, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (Exception ex){
            ex.printStackTrace();
            return new RegisterResponse("FAILED TO PUT THE PHOTO IN S3");
        }
        try {
            System.out.println("Trying to get into dynamo tables");
            DynamoDbTable<UserTableBean> followsDynamoDbTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            UserTableBean user =  new UserTableBean(request.getUsername(), request.getFirstName(), request.getLastName(), request.getImage(), request.getPassword());
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
        return new LoginResponse("FAILED TO LOGIN - GENERIC USER-DAO");
    }


    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        try {
            DynamoDbTable<UserTableBean> loginTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
            Key key = Key.builder().partitionValue(request.getAlias()).build();
            UserTableBean user = loginTable.getItem(key);
            if (user != null){
                User newUser = new User(user.firstName, user.lastName, user.alias, gets3Client().getUrl(s3BucketName, request.getAlias()).toString());
                return new GetUserResponse(newUser, request.getAuthToken());
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new GetUserResponse("FAILED TO FIND A MATCHING USER IN THE DATABASE");
        }
        return new GetUserResponse("FAILED TO GET USER - GENERIC getUser");
    }
}
