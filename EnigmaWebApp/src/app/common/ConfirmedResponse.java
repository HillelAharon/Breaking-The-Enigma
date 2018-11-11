package app.common;

public class ConfirmedResponse {
    private boolean isInputValid;
    private String outputResponse;

    public boolean isInputValid() {
        return isInputValid;
    }

    public void setInputValid(boolean requestValid) {
        isInputValid = requestValid;
    }

    public String getOutputResponse() {
        return outputResponse;
    }

    public void setOutputResponse(String textResponse) {
        this.outputResponse = textResponse;
    }
}
