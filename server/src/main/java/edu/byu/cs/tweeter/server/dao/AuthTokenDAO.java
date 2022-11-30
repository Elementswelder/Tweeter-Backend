package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.interfaces.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO extends KingDAO implements AuthTokenDAOInterface {

    private final String EXPIRE_TIME = "11/11/1 11:11:01";

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

    @Override
    public boolean expireAuthToken(String authToken) {
        System.out.println("Trying to expire an auth token: " + authToken);
        try {
            DynamoDbTable<AuthTokenBean> authDynamoDbTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));
            Key key = Key.builder().partitionValue(authToken).build();
            AuthTokenBean authTokenBean = authDynamoDbTable.getItem(key);
            authTokenBean.setDate(EXPIRE_TIME);

            authDynamoDbTable.updateItem(authTokenBean);
            return true;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}
