package net.zelcon.ibkr_api_client_wrapper.response_types;

import com.ib.client.Bar;

public class HistoricalBar extends IBResponse {
    private final Bar bar;

    public HistoricalBar(int requestId, Bar bar) {
        super(requestId);
        this.bar = bar;
    }

    public Bar getBar() {
        return bar;
    }
}
