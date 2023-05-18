package edu.uw.tcss450.kylerr10.chatapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import edu.uw.tcss450.kylerr10.chatapp.ConversationActivity;
import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass. This fragment displays a list of chat rooms
 * and allows the user to click on a room to view its conversation.
 * @author Leyla Ahmed
 */
public class ChatFragment extends Fragment implements ChatRoomAdapter.OnChatRoomClickListener {

    /**
     * List of chat rooms used by the ChatRoomAdapter to display the list of chat rooms in a RecyclerView.
     */
    private List<ChatRoom> mChatRooms;

    /**
     * The ChatRoomAdapter instance used to populate the RecyclerView with chat room data.
     */
    private ChatRoomAdapter mAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create some mock data for the chat rooms
        mChatRooms = new ArrayList<>();

        for(int i = 1; i <= 20; i++) {
            mChatRooms.add(new ChatRoom("Chat Room " + i, "Last message in Chat Room " + i));

        }
        //Access JWT
        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        String jwt = model.getJWT().toString();
        //Log.i("JWT", jwt);

        // Create a new ChatRoomAdapter and set it on the RecyclerView
        mAdapter = new ChatRoomAdapter(mChatRooms);
        mAdapter.setOnChatRoomClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set up the RecyclerView with the chat rooms
        RecyclerView recyclerView = view.findViewById(R.id.list_chat_room);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onChatRoomClick(ChatRoom chatRoom) {
        // Handle the click event for the chat room
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("chatRoomName", chatRoom.getRoomName());
        startActivity(intent);


    }

}