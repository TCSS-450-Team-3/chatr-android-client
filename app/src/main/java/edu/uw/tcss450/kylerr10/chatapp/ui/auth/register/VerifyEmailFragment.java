package edu.uw.tcss450.kylerr10.chatapp.ui.auth.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentVerifyEmailBinding;

/**
 * Fragment for user email verification.
 *
 * @author Kyler Robison, Betelhem
 */
public class VerifyEmailFragment extends Fragment {

    private FragmentVerifyEmailBinding mBinding;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using the binding object
        mBinding = FragmentVerifyEmailBinding.inflate(inflater, container, false);
        // Return the root view from the binding object
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("VerificationFragment", "onViewCreated called");
        super.onViewCreated(view, savedInstanceState);
        String url = "http://10.0.2.2:5000/verify/verify-otp";

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("email")) {
            String email = bundle.getString("email");
            setEmail(email);
        }
        mBinding.buttonVerify.setOnClickListener(button -> {
            String otpCode = mBinding.editCode.getText().toString();
            Log.d("VerificationFragment", "OTP Code: " + otpCode);
            Log.d("VerificationFragment", "Email: " + email);
            // Create the request body
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
                requestBody.put("otp", otpCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Request Body", requestBody.toString());
            // Make a request to verify the OTP code
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // OTP code verified successfully
                            Toast.makeText(requireContext(), "OTP verified successfully", Toast.LENGTH_SHORT).show();
                            Log.d("Response", response.toString());
                            // Proceed to the home fragment
                            Navigation.findNavController(requireView()).navigate(R.id.action_verifyEmailFragment_to_homeActivity);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Failed to verify OTP", Toast.LENGTH_SHORT).show();
                            Log.e("Volley", error.toString());
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    // Add authentication headers
                    headers.put("Authorization", "Bearer Chat&%$key8329dogsrule");
                    return headers;
                }
            };

            // Add the request to the request queue
            Volley.newRequestQueue(requireContext()).add(request);
        });
    }

    public void setEmail(String email) {
        Log.d("VerificationFragment", "setEmail invoked with email: " + email);
        this.email = email;
    }
}

