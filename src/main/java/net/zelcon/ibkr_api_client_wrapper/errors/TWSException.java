package net.zelcon.ibkr_api_client_wrapper.errors;

import java.util.Optional;

public class TWSException extends Exception {
    private final int code;
    private final String message;
    private Optional<String> advancedOrderRejectJson = Optional.empty();

    public TWSException(final int code, final String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public TWSException(Throwable e) {
        super(e);
        this.code = -1;
        this.message = e.getMessage();
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Optional<String> getAdvancedOrderRejectJson() {
        return advancedOrderRejectJson;
    }

    public void setAdvancedOrderRejectJson(String advancedOrderRejectJson) {
        this.advancedOrderRejectJson = Optional.of(advancedOrderRejectJson);
    }
}
