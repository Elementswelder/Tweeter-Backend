package edu.byu.cs.tweeter.server.dao.pojobeans;

import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class UserTableBean {

    public String alias;
    public String firstName;
    public String lastName;
    public String image;
    public String password;
    public int followers;
    public int following;


    public UserTableBean(){}

    public UserTableBean(String userAlias, String firstName, String lastName, String image,
                         String password, int followers, int following){
        this.firstName = firstName;
        this.image = image;
        this.alias = userAlias;
        this.lastName = lastName;
        this.password = password;
        this.followers = followers;
        this.following = following;
    }

    public UserTableBean(User u) {
        this.alias = u.getAlias();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.image = u.getImageUrl();
    }

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}
