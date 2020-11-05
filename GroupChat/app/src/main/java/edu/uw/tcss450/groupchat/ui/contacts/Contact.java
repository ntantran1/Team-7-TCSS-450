package edu.uw.tcss450.groupchat.ui.contacts;

public class Contact {

    private String mUsername;

    private String mName;

    private String mEmail;

    public Contact() {
        mUsername = "Blank";
        mName = "Blank Name";
        mEmail = "test@fakemail.com";
    }

    public Contact(final String username, final String name, final String email) {
        mUsername = username;
        mName = name;
        mEmail = email;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }
}
