package net.zelcon.ibkr_api_client_wrapper.response_types;

public class FundamentalCalendarEvent extends IBResponse {
    private final String dataJson;

    public FundamentalCalendarEvent(final int requestId, final String dataJson) {
        super(requestId);
        this.dataJson = dataJson;
    }
    
    public String getDataJson() {
        return dataJson;
    }
}
