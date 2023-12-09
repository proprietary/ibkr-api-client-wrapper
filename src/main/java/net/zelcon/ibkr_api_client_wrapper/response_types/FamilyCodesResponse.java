package net.zelcon.ibkr_api_client_wrapper.response_types;

import com.ib.client.FamilyCode;

public class FamilyCodesResponse extends IBResponse {
    private final FamilyCode[] familyCodes;

    public FamilyCodesResponse(final FamilyCode[] familyCodes) {
        super(NO_REQUEST_ID);
        this.familyCodes = familyCodes;
    }

    public FamilyCode[] getFamilyCodes() {
        return this.familyCodes;
    }
}
