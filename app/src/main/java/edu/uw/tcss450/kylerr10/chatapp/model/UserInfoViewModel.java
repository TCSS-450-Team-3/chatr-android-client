package edu.uw.tcss450.kylerr10.chatapp.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.auth0.android.jwt.JWT;

/**
 * Stores information about the logged in user to be used across the app.
 *
 * @author Kyler Robison
 */
public class UserInfoViewModel extends AndroidViewModel {
    private JWT mJwt;

    public UserInfoViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Sets the JWT of the ViewModel
     * @param jwt the token to set to.
     */
    public void setToken(JWT jwt) {
        mJwt = jwt;
    }

    /**
     * Get the email address that is stored in the payload of the JWT this ViewModel holds.
     *
     * @return the email stored in the JWT this ViewModel holds
     * @throws IllegalStateException when the JWT stored in this ViewModel is expired
     */
    public String getEmail() {
        if (!mJwt.isExpired(999999999)) {
            return mJwt.getClaim("email").asString();
        } else {
            throw new IllegalStateException("JWT is expired!");
        }
    }

    /**
     * @return the JWT that this ViewModel stores.
     * @throws IllegalStateException when the JWT stored in this ViewModel is expired.
     */
    public JWT getJWT() {
        if (!mJwt.isExpired(999999999)) {
            return mJwt;
        } else {
            throw new IllegalStateException("JWT is expired!");
        }
    }
}
