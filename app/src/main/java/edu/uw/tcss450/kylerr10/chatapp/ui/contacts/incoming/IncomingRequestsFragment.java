package edu.uw.tcss450.kylerr10.chatapp.ui.contacts.incoming;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentIncomingRequestsBinding;
import edu.uw.tcss450.kylerr10.chatapp.listdata.Contact;
import edu.uw.tcss450.kylerr10.chatapp.ui.contacts.ContactsViewModel;

/**
 * Fragment where the user can view incoming contact requests.
 *
 * @author Kyler Robison
 */
public class IncomingRequestsFragment extends Fragment {
    /**
     * Flag used to prevent the response handler from being called the first time it is set to
     * observe the response.
     */
    private boolean observerCreated = false;

    private FragmentIncomingRequestsBinding mBinding;

    private ContactsViewModel mContactsViewModel;

    /**
     * The unfiltered list of requests
     */
    private List<Contact> mfullItemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactsViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentIncomingRequestsBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mContactsViewModel.connectGetIncoming();
    }

    /**
     * Events for when the fragment and its views are created.
     * Also handles building of the RecyclerView that displays current contacts.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContactsViewModel.addAcceptContactResponseObserver(getViewLifecycleOwner(),
                response -> mContactsViewModel.updateContacts());

        mContactsViewModel.addDeleteContactResponseObserver(getViewLifecycleOwner(),
                response -> mContactsViewModel.updateContacts());

        mContactsViewModel.addGetIncomingResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        mContactsViewModel.getSearchText().observe(getViewLifecycleOwner(), this::filterList);
    }

    private void filterList(String searchText) {
        if (mfullItemsList == null) return;

        List<Contact> filteredList;

        if (searchText != null) {
            filteredList = mfullItemsList.stream()
                    .filter(item -> {
                        String regex = "(?i)" + Pattern.quote(searchText);
                        return Pattern.compile(regex).matcher(item.mUsername).find();
                    })
                    .collect(Collectors.toList());
        } else {
            filteredList = mfullItemsList;
        }

        mBinding.recyclerViewIncomingRequests.setAdapter(
                new IncomingRequestsRecyclerViewAdapter(mContactsViewModel, filteredList)
        );

        if(filteredList.size() == 0) {
            mBinding.labelNoItems.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.labelNoItems.setVisibility(View.GONE);
        }
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to the ViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if(!observerCreated) { //Prevents method from executing just because the observer is added
            observerCreated = true;
            return;
        }
        if (response.length() > 0) {
            if (response.has("code")) {
                showErrorNotification();
            } else {
                try {
                    processResponse(response);
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                    showErrorNotification();
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
            showErrorNotification();
        }
    }

    /**
     * Processes the response JSON, repopulates recyclerview.
     */
    private void processResponse(final JSONObject response) throws JSONException {
        ArrayList<Contact> contactsList = new ArrayList<>();

        JSONArray contactsArray = response.getJSONArray("contacts");
        for(int i = 0; i < contactsArray.length(); i++) {
            JSONObject contactObject = contactsArray.getJSONObject(i);

            int connectionId = contactObject.getInt("connectionid");
            String username = contactObject.getString("username");
            String email = contactObject.getString("email");
            String firstName = contactObject.getString("firstname");
            String lastName = contactObject.getString("lastname");

            Contact c = new Contact(connectionId, username, email, firstName, lastName);
            contactsList.add(c);
        }

        mfullItemsList = contactsList;

        mBinding.recyclerViewIncomingRequests.setAdapter(
                new IncomingRequestsRecyclerViewAdapter(mContactsViewModel, contactsList)
        );

        if(contactsList.size() == 0) {
            mBinding.labelNoItems.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.labelNoItems.setVisibility(View.GONE);
        }

        filterList(mContactsViewModel.getSearchText().getValue());
    }

    /**
     * Displays an error notification to the user.
     */
    private void showErrorNotification() {
        Snackbar.make(requireView(), "An error occurred", Snackbar.LENGTH_SHORT).show();
    }
}