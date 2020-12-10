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

public class ChatEditText extends androidx.appcompat.widget.AppCompatEditText {

    private KeyBoardInputCallbackListener keyBoardInputCallbackListener;

    public ChatEditText(Context context) {
        super(context);
    }

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


    public interface KeyBoardInputCallbackListener {
        void onCommitContent(InputContentInfoCompat inputContentInfo,
                             int flags, Bundle opts);
    }

    public void setKeyBoardInputCallbackListener(KeyBoardInputCallbackListener keyBoardInputCallbackListener) {
        this.keyBoardInputCallbackListener = keyBoardInputCallbackListener;
    }

}