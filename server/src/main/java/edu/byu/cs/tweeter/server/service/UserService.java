package edu.byu.cs.tweeter.server.service;

import com.amazonaws.Response;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.request.FollowerCountRequest;
import edu.byu.cs.tweeter.request.FollowingCountRequest;
import edu.byu.cs.tweeter.response.FollowerCountResponse;
import edu.byu.cs.tweeter.response.FollowingCountResponse;
import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.response.RegisterResponse;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.LogoutRequest;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.LogoutResponse;
import edu.byu.cs.tweeter.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.Hasher;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.UserTableBean;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {


    private DAOFactoryInterface factoryInterface;

    public UserService(DAOFactoryInterface factoryInterface){
        this.factoryInterface = factoryInterface;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        //UNCOMMENT ON RELEASE
       LoginResponse response = factoryInterface.getUserDAO().loginUser(request);
        if (response.isSuccess()){
            //If not added the authtoken to the table, fail the request
            if (!factoryInterface.getAuthTokenDAO().addAuthToken(response.getAuthToken().getToken(), response.getAuthToken().getDatetime(), request.getUsername())){
                return new LoginResponse("FAILED TO ADD AUTH TOKEN TO THE TABLE - LOGIN");
            }
        }
        return response;


       // User user = getFakeData().getFirstUser();
       // return new LoginResponse(user, getDummyAuthToken());
    }

    public LogoutResponse logout(LogoutRequest request){
        boolean success = factoryInterface.getAuthTokenDAO().expireAuthToken(request.getAuthToken().getToken(), request.getUserAlias());
        if (!success){
            return new LogoutResponse("FAILED TO UPDATE THE AUTH TOKEN");
        }
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if (request.getFirstName() == null){
            throw new RuntimeException("[Bad Request] Missing First Name");
        }
        else if (request.getFirstName() == null){
            throw new RuntimeException("[Bad Request] Missing First Name");
        }
        else if (request.getImage() == null){
            throw new RuntimeException("[Bad Request] Missing Image");
        }
        //HashPassword before continuing
        request.setPassword(Hasher.generateStrongPasswordHash(request.getPassword()));
        RegisterResponse response = factoryInterface.getUserDAO().registerUser(request);
        //If response succeeds, then add the authtoken to the table with the date
        if (response.isSuccess()){
            //If not added the authtoken to the table, fail the request
            if (!factoryInterface.getAuthTokenDAO().addAuthToken(response.getAuthToken().getToken(), response.getAuthToken().getDatetime(), request.getUsername())){
                return new RegisterResponse("FAILED TO ADD AUTH TOKEN TO THE TABLE - REGISTER");
            }
        }
        return response;
    }

    public GetUserResponse getUser(GetUserRequest request){
        // if (!checkValidAuth(request.getAuthToken().getToken())){
        //   return new GetUserResponse("AuthToken Expired, please log in again");
        //}
        if (request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing the Alias");
        }
        GetUserResponse response = factoryInterface.getUserDAO().getUser(request);
        if (!response.isSuccess()){
            return new GetUserResponse("FAILED TO FIND THE USER - GET USER");
        }
        return response;
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        // if (!checkValidAuth(request.getAuthToken().getToken())){
        //   return new FollowingCountResponse("AuthToken Expired, please log in again");
        //}
        return factoryInterface.getUserDAO().getFollowingCount(request);
    }

    //The people following the user;
    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        // if (!checkValidAuth(request.getAuthToken().getToken())){
        //   return new FollowingCountResponse("AuthToken Expired, please log in again");
        //}
        return factoryInterface.getUserDAO().getFollowerCount(request);
    }




    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
