package net.zelcon.ibkr_api_client_wrapper.errors;

public class TWSConnectionTimeoutException extends Exception {
    public TWSConnectionTimeoutException() {
        super("Attempt to connect to TWS timed out.");
    }
}
