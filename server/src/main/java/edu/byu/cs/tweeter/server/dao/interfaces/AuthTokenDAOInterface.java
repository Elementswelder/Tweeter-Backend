package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.Response;

public interface AuthTokenDAOInterface {

    abstract boolean addAuthToken(String auth, String date, String alias);
    abstract boolean expireAuthToken(String AuthToken, String userAlias);


}
