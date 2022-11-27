package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.interfaces.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO extends KingDAO implements AuthTokenDAOInterface {


    @Override
    public boolean addAuthToken(String auth, String date, String alias) {
        try {
            System.out.println("Trying to get into dynamo tables - AuthTokenDAO");
            DynamoDbTable<AuthTokenBean> authDynamoDbTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));

            AuthTokenBean authToken =  new AuthTokenBean(auth, date, alias);
            authDynamoDbTable.putItem(authToken);

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
