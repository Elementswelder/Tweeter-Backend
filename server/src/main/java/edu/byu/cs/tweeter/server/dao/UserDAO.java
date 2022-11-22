package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.RegisterResponse;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;


public class UserDAO extends KingDAO implements UserDAOInterface {

    User user;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        System.out.println("Trying to upload to S3 Bucket");
        try {
            gets3Client().putObject(s3BucketName, request.getImage(), request.getUsername() + "-profile");
        } catch (Exception ex){
            return new RegisterResponse("FAILED TO PUT THE PHOTO IN S3");
        }
        try {
            DynamoDbTable<UserTableBean> followsDynamoDbTable = getDbClient().table("UserTable", TableSchema.fromBean(UserTableBean.class));
          // User user =  new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());
          //  followsDynamoDbTable.putItem(user);
        } catch (Exception ex){
            return new RegisterResponse("FAILED TO ADD A USER TO THE TABLE");
        }
        return new RegisterResponse(user, new AuthToken("test USERDAOREIGSTER", "TIME"));
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        return null;
    }
}
