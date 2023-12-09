package net.zelcon.ibkr_api_client_wrapper.response_types;

public class ManagedAccountsResponse extends IBResponse {
    private final String managedAccounts;

    public ManagedAccountsResponse(final String managedAccounts) {
        super(NO_REQUEST_ID);
        this.managedAccounts = managedAccounts;
    }

    public String getManagedAccounts() {
        return this.managedAccounts;
    }
}
