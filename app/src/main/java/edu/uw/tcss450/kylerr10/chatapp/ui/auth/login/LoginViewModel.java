package edu.uw.tcss450.kylerr10.chatapp.ui.auth.login;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.kylerr10.chatapp.BuildConfig;
import edu.uw.tcss450.kylerr10.chatapp.io.RequestQueueSingleton;

/**
 * A simple {@link ViewModel} subclass responsible for handling HTTP requests regarding user
 * credentials.
 *
 * @author Kyler Robison
 */
public class LoginViewModel extends AndroidViewModel {

    /**
     * Email of the user.
     */
    private String mUserEmail;

    /**
     * Password of the user.
     */
    private String mUserPassword;

    private MutableLiveData<JSONObject> mResponse;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Sets the email of the user.
     * @param email the new email.
     */
    public void setUserEmail(String email) {
        mUserEmail = email;
    }

    /**
     * @return the user's email.
     */
    public String getUserEmail() {
        return mUserEmail;
    }

    /**
     * Sets the password of the user.
     * @param password the new password.
     */
    public void setUserPassword(String password) {
        mUserPassword = password;
    }

    /**
     * Makes the api call to log in the user using the credentials currently stored in this object.
     */
    public void connect() {
        String url = BuildConfig.BASE_URL + "/auth";
        Request<JSONObject> request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                mResponse::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                String credentials = mUserEmail + ":" + mUserPassword;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * Handles errors from the login request.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", error.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Makes the api call to reset the user's password.
     * @param email the email of the user.
     */
    public void connectPutReset(String email) {
        String url = BuildConfig.BASE_URL + "/auth/reset";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            Log.e("Auth Reset PUT ERROR", e.getMessage());
        }
        Request<JSONObject> request = new JsonObjectRequest(Request.Method.PUT, url, body, null, this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }
}