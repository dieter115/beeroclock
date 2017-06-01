package be.flashapps.beeroclock.Models;

/**
 * Created by dietervaesen on 16/11/16.
 */

public class ApiError {
    String exception;
    String message;
    String hint;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
