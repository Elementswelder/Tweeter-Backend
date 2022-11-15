package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FeedDAO;

public class KingHandler {

    protected DAOFactoryInterface factoryInterface;

    protected DAOFactoryInterface getFactoryInterface() {
        if (factoryInterface == null) {
            factoryInterface = new DAOFactory();
        }
        return factoryInterface;
    }

}
