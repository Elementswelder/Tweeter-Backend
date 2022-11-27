package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;

public class KingHandler {

    protected DAOFactoryInterface factoryInterface;

    public DAOFactoryInterface getFactoryInterface() {
        if (factoryInterface == null) {
            factoryInterface = new DAOFactory();
        }
        return factoryInterface;
    }

}
