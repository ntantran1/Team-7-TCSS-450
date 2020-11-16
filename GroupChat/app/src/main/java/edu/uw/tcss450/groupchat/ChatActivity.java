package edu.uw.tcss450.groupchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import edu.uw.tcss450.groupchat.ui.chats.ChatListAdapter;
import edu.uw.tcss450.groupchat.ui.chats.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChatRecycler;
    private ChatListAdapter mChatAdapter;
    public List<ChatMessage> chatList;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatRecycler = (RecyclerView) findViewById(R.id.reyclerview_chat_list);

        //this might need to be changed.
        mChatAdapter = new ChatListAdapter(this, chatList);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}