package net.zelcon.ibkr_api_client_wrapper.response_types;

public abstract class IBResponse {
    private final int requestId;

    public IBResponse(final int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}
