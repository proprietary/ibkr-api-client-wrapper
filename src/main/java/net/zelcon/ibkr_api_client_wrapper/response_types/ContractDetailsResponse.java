package net.zelcon.ibkr_api_client_wrapper.response_types;

import com.ib.client.*;

public class ContractDetailsResponse extends IBResponse {
    private final ContractDetails contractDetails;

    public ContractDetailsResponse(final int requestId, final ContractDetails contractDetails) {
        super(requestId);
        this.contractDetails = contractDetails;
    }
    
    public ContractDetails getContractDetails() {
        return contractDetails;
    }
}
