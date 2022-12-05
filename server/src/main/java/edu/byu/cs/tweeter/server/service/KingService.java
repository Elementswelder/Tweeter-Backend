package edu.byu.cs.tweeter.server.service;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;


public class KingService {

    protected DAOFactoryInterface factoryInterface;

    public KingService(DAOFactoryInterface factoryInterface){
        this.factoryInterface = factoryInterface;
    }

    public boolean checkValidAuth(String authToken){
        try {
            return factoryInterface.getAuthTokenDAO().checkValidAuth(authToken);
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
