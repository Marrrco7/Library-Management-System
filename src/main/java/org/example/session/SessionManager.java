package org.example.session;

import org.example.model.User;




/**
 * This class manages session state for the currently logged in user.
 *
 * <p>This class provides static methods to set, retrieve, check login status,
 * and logout the current user. It stores the logged in user in a static field,
 * making the session information accessible throughout the application.</p>
 */


public class SessionManager {
    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static void logout() {
        loggedInUser = null;
    }
}
