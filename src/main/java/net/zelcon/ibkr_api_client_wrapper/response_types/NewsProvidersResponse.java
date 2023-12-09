package net.zelcon.ibkr_api_client_wrapper.response_types;

import com.ib.client.NewsProvider;

public class NewsProvidersResponse extends IBResponse {
    private final NewsProvider[] newsProviders;

    public NewsProvidersResponse(final NewsProvider[] newsProviders) {
        super(NO_REQUEST_ID);
        this.newsProviders = newsProviders;
    }

    public NewsProvider[] getNewsProviders() {
        return newsProviders;
    }
}
