package edu.byu.cs.tweeter.server.dao.pojobeans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AuthTokenBean {

    public String auth_token;
    public String date;
    public String alias;


    public AuthTokenBean(){}

    public AuthTokenBean(String auth_token, String date, String alias){
        this.auth_token = auth_token;
        this.date = date;
        this.alias = alias;
    }

    @DynamoDbPartitionKey
    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlias() {
        return alias;
    }


}
