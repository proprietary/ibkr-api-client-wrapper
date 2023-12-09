package net.zelcon.ibkr_api_client_wrapper.response_types;

public class CurrentTimeResponse extends IBResponse {
    private final long currentTime;

    public CurrentTimeResponse(final long currentTime) {
        super(NO_REQUEST_ID);
        this.currentTime = currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }
}