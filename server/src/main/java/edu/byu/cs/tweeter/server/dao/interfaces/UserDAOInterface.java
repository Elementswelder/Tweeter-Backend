package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.request.GetUserRequest;
import edu.byu.cs.tweeter.response.GetUserResponse;
import edu.byu.cs.tweeter.response.LoginResponse;
import edu.byu.cs.tweeter.response.RegisterResponse;
import edu.byu.cs.tweeter.request.LoginRequest;
import edu.byu.cs.tweeter.request.RegisterRequest;

public interface UserDAOInterface {

    abstract RegisterResponse registerUser(RegisterRequest request);
    abstract LoginResponse loginUser(LoginRequest request);
    abstract GetUserResponse getUser(GetUserRequest request);

}
