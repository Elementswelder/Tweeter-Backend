package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class TheGreatTenGrand {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 400;

    // how many leading 0s we need on the user, based on how large NUM_USERS is
    private final static int NUM_PADDING = 5;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@FreddyMan";

    public static void main(String[] args) {
        TheGreatTenGrand.fillDatabase();
    }

    public static void fillDatabase() {

        // Preferably, get instance of DAOs by way of the Abstract Factory Pattern or DI
        UserDAO userDAO = new UserDAO();
        FollowDAO followDAO = new FollowDAO();

        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String paddedNumber = String.format("%0" + NUM_PADDING + "d", i);

            String firstName = "Dude" + paddedNumber;
            String lastName = "LastName" + paddedNumber;
            String alias = "@dude" + paddedNumber;
            String imageUrl = "https://i.imgur.com/hE1pTMb.jpg";

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User(firstName, lastName, alias, imageUrl);
           users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
       /* if (users.size() > 0) {
            userDAO.addUserBatch(users);
        } */
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(users);
        }
    }

}
