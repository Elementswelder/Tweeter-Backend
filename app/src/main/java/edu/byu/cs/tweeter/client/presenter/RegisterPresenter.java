package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class RegisterPresenter extends LoginsPresenter {

    String imageToString;

    public RegisterPresenter(LoginsView view) {
        super(view);
    }

    public void initiateRegister(String firstName, String lastName, String username, String password) {
        String message = validateRegister(firstName, lastName, username, password); // This should go to the presenter
        if (clearAndDisplayLogin(message, "Registering")){
            userService.register(firstName,lastName,username,password,this.imageToString, new LoginRegisterObserver());
        }
    }

    public String validateRegister(String firstName, String lastName, String username, String password) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    public void checkImage(Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap image = bitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            this.imageToString = Base64.getEncoder().encodeToString(imageBytes);
        }
        else {
            view.clearMessage();
            view.displayErrorMessage("Error with the image");
        }
    }
}
