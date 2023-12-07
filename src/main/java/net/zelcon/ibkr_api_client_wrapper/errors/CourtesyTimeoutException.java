package net.zelcon.ibkr_api_client_wrapper.errors;

public class CourtesyTimeoutException extends Exception {
    public CourtesyTimeoutException() {
        super("Cancelling request which has run on too long. This prevents getting blocked by IBKR.");
    }
}
