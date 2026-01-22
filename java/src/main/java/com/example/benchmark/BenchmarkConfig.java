package com.example.benchmark;

/**
 * ベンチマーク設定クラス
 */
public class BenchmarkConfig {

    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;
    private final String endpoint;

    public BenchmarkConfig(String clientId, String clientSecret, String baseUrl, String endpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
    }

    public static BenchmarkConfig fromEnvironment() {
        String clientId = System.getenv("ZUORA_CLIENT_ID");
        String clientSecret = System.getenv("ZUORA_CLIENT_SECRET");
        String baseUrl = System.getenv().getOrDefault("ZUORA_BASE_URL", "https://rest.apisandbox.zuora.com");
        String endpoint = System.getenv().getOrDefault("ZUORA_BENCHMARK_ENDPOINT", "/v1/catalog/products");

        return new BenchmarkConfig(clientId, clientSecret, baseUrl, endpoint);
    }

    public boolean isValid() {
        return clientId != null && !clientId.isEmpty()
                && clientSecret != null && !clientSecret.isEmpty();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
