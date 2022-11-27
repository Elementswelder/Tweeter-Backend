package edu.byu.cs.tweeter.server.service;

import com.amazonaws.Response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.response.RegisterResponse;
import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.LogoutRequest;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.LogoutResponse;
import edu.byu.cs.tweeter.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;
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

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request){
        if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing authtoken");
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
        RegisterResponse response = factoryInterface.getUserDAO().registerUser(request);

        //If response succeeds, then add the authtoken to the table with the date
        if (response.isSuccess()){
            //If not added the authtoken to the table, fail the request
            if (!factoryInterface.getAuthTokenDAO().addAuthToken(response.getAuthToken().getToken(), response.getAuthToken().getDatetime(), request.getUsername())){
                return new RegisterResponse("FAILED TO ADD AUTH TOKEN TO THE TABLE");
            }
        }
        return response;
    }

    public GetUserResponse getUser(GetUserRequest request){
        if (request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing the Alias");
        }

        User user = getFakeData().findUserByAlias(request.getAlias());
        return new GetUserResponse(user, request.getAuthToken());
    }

    private String hashPassword(String password){

        return null;
    }


    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
