package net.zelcon.ibkr_api_client_wrapper.errors;

public class TWSWaitForResponseTimeout extends Exception {
    public TWSWaitForResponseTimeout() {
        super("Timeout exceeded while waiting for response from TWS.");
    }
}
