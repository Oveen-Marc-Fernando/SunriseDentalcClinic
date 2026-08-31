package controller;

import java.util.Objects;
import model.LoginModel;
import model.User;

/**
 * Controller that mediates between the LoginForm view and the LoginModel.
 * Follows strict MVC separation of concerns — authentication logic and state management.
 *
 * @author oveen
 */
public class LogInController {

    private LoginModel loginModel;
    private User       currentUser;

    public LogInController() {
        this(new LoginModel());
    }

    public LogInController(LoginModel model) {
        this.loginModel = Objects.requireNonNull(model, "LoginModel cannot be null");
    }

    // ── Getters / setters ────────────────────────────────────────────────────

    public void setLoginModel(LoginModel model) {
        this.loginModel = Objects.requireNonNull(model, "LoginModel cannot be null");
    }

    public LoginModel getLoginModel()  { return loginModel; }
    public User       getCurrentUser() { return currentUser; }

    // ── Business logic ────────────────────────────────────────────────────────

    /**
     * Attempts to authenticate the supplied credentials.
     *
     * @param username plain-text username
     * @param password plain-text password
     * @return the authenticated {@link User} on success, {@code null} on failure
     */
    public User attemptLogin(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String cleanUser = username.trim();
        if (cleanUser.isEmpty() || password.isEmpty()) {
            return null;
        }
        this.currentUser = loginModel.authenticate(cleanUser, password);
        return this.currentUser;
    }

    /**
     * Clears the active session and logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Raw account status ("PENDING"/"APPROVED"/"REJECTED"), or {@code null}
     * if the username doesn't exist — lets a failed login tell "still
     * pending Administrator approval" apart from "wrong password", without
     * changing {@link #attemptLogin}'s own can't-tell-them-apart contract.
     */
    public String getAccountStatus(String username) {
        return username == null ? null : new dao.UserDAO().getStatus(username.trim());
    }
}
