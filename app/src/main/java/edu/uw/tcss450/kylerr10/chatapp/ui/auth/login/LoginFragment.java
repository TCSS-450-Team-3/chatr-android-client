package edu.uw.tcss450.kylerr10.chatapp.ui.auth.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.kylerr10.chatapp.ui.auth.register.VerifyEmailFragment;

/**
 * A simple {@link Fragment} subclass responsible for validating user credentials.
 * @author Jasper Newkirk,Betelhem
 */
public class LoginFragment extends Fragment {

    public FragmentLoginBinding mBinding;
    private boolean buttonClicked = false;

    private LoginViewModel mViewModel;

    private String userEmail;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentLoginBinding.bind(requireView());
        String url = "http://10.0.2.2:5000/verify/send-email";
        mBinding.buttonLogin.setOnClickListener(button -> {
            // Check if the button is already clicked
            if (buttonClicked) {
                return; // Ignore subsequent clicks
            }

            // Set the button as clicked
            buttonClicked = true;

            // Get the email entered by the user
            String email = mBinding.editEmail.getText().toString();
            userEmail = email;
            Log.d("EMAIL", email);

            VerifyEmailFragment verifyEmailFragment = new VerifyEmailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            verifyEmailFragment.setArguments(bundle);
            // Create the request body
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Make a request to send the verification email
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Email sent successfully
                            Toast.makeText(requireContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
                            Log.d("Response", response.toString());
                            // Proceed to the verification fragment
                            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_verifyEmailFragment);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Failed to send email", Toast.LENGTH_SHORT).show();
                            Log.e("Volley", error.toString());
                            buttonClicked = false;
                        }
                    }
            );

            // Add the request to the request queue
            Volley.newRequestQueue(requireContext()).add(request);
        });

        mBinding.buttonToRegister.setOnClickListener(button -> {
         Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_registerFragment);

        });

        LoginFragmentArgs args = LoginFragmentArgs.fromBundle(getArguments());
        mBinding.editEmail.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        mBinding.editPassword.setText(args.getPassword().equals("default") ? "" : args.getPassword());

    }


}