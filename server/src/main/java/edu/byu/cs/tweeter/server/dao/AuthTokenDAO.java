package edu.byu.cs.tweeter.server.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.byu.cs.tweeter.server.dao.interfaces.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.pojobeans.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO extends KingDAO implements AuthTokenDAOInterface {

    private final String EXPIRE_TIME = "11/11/1 11:11:01";

    @Override
    public boolean addAuthToken(String auth_token, String date, String alias) {
        try {
            System.out.println("Trying to get into dynamo tables - AuthTokenDAO");
            DynamoDbTable<AuthTokenBean> authDynamoDbTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));

            AuthTokenBean authToken =  new AuthTokenBean(auth_token, date, alias);
            authDynamoDbTable.putItem(authToken);

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean expireAuthToken(String authToken, String userAlias) {
        System.out.println("Trying to expire an auth token: " + authToken);
        try {
            DynamoDbTable<AuthTokenBean> authDynamoDbTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));
            Key key = Key.builder().partitionValue(authToken).sortValue(userAlias).build();
            AuthTokenBean authTokenBean = authDynamoDbTable.getItem(key);
            authTokenBean.setDate(EXPIRE_TIME);

            authDynamoDbTable.updateItem(authTokenBean);
            return true;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean checkValidAuth(String authToken){
        try {
            System.out.println("inside checkvalidauth");
            DynamoDbTable<AuthTokenBean> authTokenTable = getDbClient().table("AuthTokenTable", TableSchema.fromBean(AuthTokenBean.class));
            Key key = Key.builder().partitionValue(authToken).build();
            AuthTokenBean auth = authTokenTable.getItem(key);

            System.out.println("DEBUG - AUTHTOKEN DATE FROM TABLE: " + auth.getDate());
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            String dateNow = format.format(new Date());
            System.out.println("DEBUG - AUTHTOKEN DATE NOW: " + dateNow);

            //d1 is the originally logged in authtoken
            Date d1 = format.parse(auth.getDate());
            //d2 is the current time
            Date d2 = format.parse(dateNow);

            float floatyboi = ((d2.getTime() - d1.getTime()) / 1000) / 60;
            System.out.println("TIME AFTER MATH- MINUTES: " + floatyboi);
            if (floatyboi <= 2){
                return true;
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

}
