package edu.uw.tcss450.groupchat.ui.contacts;

/**
 *
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
     * 
     * @return
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return mEmail;
    }
}
