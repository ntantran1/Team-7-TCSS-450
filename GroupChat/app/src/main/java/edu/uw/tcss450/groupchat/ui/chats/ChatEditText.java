package edu.uw.tcss450.groupchat.ui.chats;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

/**
 * A custom EditText component for the chat room to send media files.
 *
 * @version December 2020
 */
public class ChatEditText extends androidx.appcompat.widget.AppCompatEditText {

    private KeyBoardInputCallbackListener keyBoardInputCallbackListener;

    /**
     * Public constructor accepts application context.
     *
     * @param context reference context of the application
     */
    public ChatEditText(Context context) {
        super(context);
    }

    /**
     * Public constructor accepts application context and attribute set.
     *
     * @param context reference context of the application
     * @param attributeSet attribute set of view
     */
    public ChatEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/*"});

        final InputConnectionCompat.OnCommitContentListener callback =
                (inputContentInfo, flags, opts) -> {
                    // read and display inputContentInfo asynchronously
                    if (BuildCompat.isAtLeastNMR1() && (flags &
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            return false; // return false if failed
                        }
                    }
                    // read and display inputContentInfo asynchronously.
                    if (keyBoardInputCallbackListener != null) {
                        keyBoardInputCallbackListener.onCommitContent(inputContentInfo, flags, opts);
                    }

                    // call inputContentInfo.releasePermission() as needed.
                    return true;  // return true if succeeded
                };
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    /**
     * Interface for the user class to define the action after media input is received from keyboard.
     *
     * @version December, 2020
     */
    public interface KeyBoardInputCallbackListener {
        /**
         * Action on media input received from keyboard.
         * Designed to work with InputConnectionCompat.OnCommitContentListener
         *
         * @param inputContentInfo Info Compat object of content
         * @param flags flags
         * @param opts Bundle options
         */
        void onCommitContent(InputContentInfoCompat inputContentInfo,
                             int flags, Bundle opts);
    }

    /**
     * Method for user to define action of the callback interface.
     *
     * @param keyBoardInputCallbackListener interface object with overridden conCommitContent method
     */
    public void setKeyBoardInputCallbackListener(KeyBoardInputCallbackListener keyBoardInputCallbackListener) {
        this.keyBoardInputCallbackListener = keyBoardInputCallbackListener;
    }

}