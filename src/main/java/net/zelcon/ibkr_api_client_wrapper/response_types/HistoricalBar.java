package net.zelcon.ibkr_api_client_wrapper.response_types;

import java.text.MessageFormat;

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

    @Override
    public String toString() {
        return MessageFormat.format("HistoricalBar(requestId={0}, bar={1})", getRequestId(), stringifyBar());
    }

    private String stringifyBar() {
        return MessageFormat.format(
                "Bar(time={0}, open={1}, high={2}, low={3}, close={4}, volume={5}, wap={6}, count={7})",
                bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.wap(), bar.count());
    }
}
