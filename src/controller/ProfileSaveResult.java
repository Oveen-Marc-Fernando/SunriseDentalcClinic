package controller;

/**
 * Outcome of an Edit Profile save. Username and Password are changed
 * independently of each other (either, both, or neither may have actually
 * been requested), so the result reports each one separately instead of
 * collapsing everything into one boolean — a partial success (e.g. password
 * changed but the new username was already taken) needs to say so honestly
 * rather than claiming full success or full failure.
 *
 * @author oveen
 */
public final class ProfileSaveResult {

    public enum Field { UNCHANGED, SUCCESS, USERNAME_TAKEN, FAILED }

    public final Field usernameOutcome;
    public final Field passwordOutcome;

    public ProfileSaveResult(Field usernameOutcome, Field passwordOutcome) {
        this.usernameOutcome = usernameOutcome;
        this.passwordOutcome = passwordOutcome;
    }

    /** True only if nothing that was actually requested failed. */
    public boolean isFullSuccess() {
        return usernameOutcome != Field.USERNAME_TAKEN && usernameOutcome != Field.FAILED
                && passwordOutcome != Field.FAILED;
    }

    /** True if at least one field was actually changed in the database. */
    public boolean anythingChanged() {
        return usernameOutcome == Field.SUCCESS || passwordOutcome == Field.SUCCESS;
    }

    /** Builds a human-readable summary for a popup — lists every field that changed or failed. */
    public String summarize() {
        StringBuilder sb = new StringBuilder();
        if (usernameOutcome == Field.SUCCESS) {
            sb.append("Username updated.\n");
        } else if (usernameOutcome == Field.USERNAME_TAKEN) {
            sb.append("That username is already taken.\n");
        } else if (usernameOutcome == Field.FAILED) {
            sb.append("Couldn't update the username.\n");
        }

        if (passwordOutcome == Field.SUCCESS) {
            sb.append("Password updated.\n");
        } else if (passwordOutcome == Field.FAILED) {
            sb.append("Couldn't update the password.\n");
        }

        return sb.length() == 0 ? "No changes made." : sb.toString().trim();
    }
}
