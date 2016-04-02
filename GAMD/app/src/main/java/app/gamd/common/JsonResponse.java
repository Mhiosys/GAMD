package app.gamd.common;

/**
 * Created by Sigcomt on 30/03/2016.
 */
public class JsonResponse {
    private String Message;
    private boolean Success;
    private Object Data;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }
}
