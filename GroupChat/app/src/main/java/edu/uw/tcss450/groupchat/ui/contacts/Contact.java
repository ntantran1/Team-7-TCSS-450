package edu.uw.tcss450.groupchat.ui.contacts;

/**
 * Object for storing user contact information.
 * @author Dylan Hill
 * @version November 2020
 */
public class Contact {

    private String mUsername;

    private String mName;

    private String mEmail;

    /**
     * Generic constructor, should NEVER be called.
     */
    public Contact() {
        mUsername = "Blank";
        mName = "Blank Name";
        mEmail = "test@fakemail.com";
    }

    /**
     * Contact class constructor, initializes the contact with the passed arguments.
     * @param username the username of the contact
     * @param name the name (first and last) of the contact
     * @param email the email of the contact
     */
    public Contact(final String username, final String name, final String email) {
        mUsername = username;
        mName = name;
        mEmail = email;
    }

    /**
     * Returns the contact's username.
     * @return the contact username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Returns the contact's first and last name.
     * @return the contact first and last name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the contact's email.
     * @return the contact email
     */
    public String getEmail() {
        return mEmail;
    }
}
