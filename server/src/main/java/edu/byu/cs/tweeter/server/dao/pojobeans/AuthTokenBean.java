package edu.byu.cs.tweeter.server.dao.pojobeans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class AuthTokenBean {

    public String authtoken;
    public String date;
    public String alias;

    public AuthTokenBean(){}

    public AuthTokenBean(String auth, String date, String alias){
        this.authtoken = auth;
        this.date = date;
        this.alias = alias;
    }

    @DynamoDbPartitionKey
    public String getauthtoken() {
        return authtoken;
    }

    public void setauthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDbSortKey
    public String getalias() {
        return alias;
    }

    public void setalias(String alias) {
        this.alias = alias;
    }
}
