package com.example.benchmark;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

/**
 * Zuora APIベンチマーク実行クラス
 *
 * TODO: Zuora SDK が利用可能になったら SDK 経由に置き換える
 * 現在は HTTP クライアントを使用した直接呼び出しで実装
 */
public class ZuoraBenchmark {

    private final BenchmarkConfig config;
    private final HttpClient httpClient;
    private String accessToken;
    private long tokenExpiresAt;

    public ZuoraBenchmark(BenchmarkConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * 単一のベンチマーク反復を実行
     */
    public void runSingleIteration() throws Exception {
        // トークンが期限切れまたは未取得の場合は認証
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiresAt) {
            authenticate();
        }

        // APIエンドポイントを呼び出し
        callApi();
    }

    /**
     * OAuth認証でアクセストークンを取得
     */
    private void authenticate() throws Exception {
        String credentials = config.getClientId() + ":" + config.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + "/oauth/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedCredentials)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Authentication failed: " + response.statusCode() + " - " + response.body());
        }

        // シンプルなJSONパース（依存関係を減らすため）
        String body = response.body();
        accessToken = extractJsonValue(body, "access_token");
        String expiresIn = extractJsonValue(body, "expires_in");

        // トークン有効期限を設定（余裕を持って10秒前に期限切れとする）
        long expiresInSeconds = Long.parseLong(expiresIn);
        tokenExpiresAt = System.currentTimeMillis() + (expiresInSeconds - 10) * 1000;
    }

    /**
     * 指定されたAPIエンドポイントを呼び出し
     */
    private void callApi() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + config.getEndpoint()))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("API call failed: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * JSONから値を抽出するシンプルなヘルパー
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            throw new RuntimeException("Key not found in JSON: " + key);
        }

        int colonIndex = json.indexOf(":", keyIndex);
        int valueStart = colonIndex + 1;

        // 空白をスキップ
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            // 文字列値
            int valueEnd = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // 数値やその他の値
            int valueEnd = valueStart;
            while (valueEnd < json.length() && !",}]".contains(String.valueOf(json.charAt(valueEnd)))) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }
}
