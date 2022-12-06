package edu.byu.cs.tweeter.server.dao.pojobeans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AuthTokenBean {

    public String authtoken;
    public String date;
    public String alias;


    public AuthTokenBean() {}

    public AuthTokenBean(String authtoken, String date, String alias){
        this.authtoken = authtoken;
        this.date = date;
        this.alias = alias;
    }

    @DynamoDbPartitionKey
    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
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
