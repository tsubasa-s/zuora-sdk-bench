package com.example.benchmark;

import java.util.Collections;
import java.util.List;

/**
 * ベンチマーク結果クラス
 */
public class BenchmarkResult {

    private final String sdk;
    private final List<Long> latencies;
    private final long totalDurationMs;
    private final int iterations;

    public BenchmarkResult(String sdk, List<Long> latencies, long totalDurationMs, int iterations) {
        this.sdk = sdk;
        this.latencies = latencies;
        this.totalDurationMs = totalDurationMs;
        this.iterations = iterations;
        Collections.sort(this.latencies);
    }

    public String getSdk() {
        return sdk;
    }

    public int getIterations() {
        return iterations;
    }

    public long getTotalDurationMs() {
        return totalDurationMs;
    }

    public double getThroughput() {
        return (iterations * 1000.0) / totalDurationMs;
    }

    public long getMinLatency() {
        return latencies.isEmpty() ? 0 : latencies.get(0);
    }

    public long getMaxLatency() {
        return latencies.isEmpty() ? 0 : latencies.get(latencies.size() - 1);
    }

    public double getMeanLatency() {
        return latencies.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public long getP50Latency() {
        return getPercentile(50);
    }

    public long getP90Latency() {
        return getPercentile(90);
    }

    public long getP95Latency() {
        return getPercentile(95);
    }

    public long getP99Latency() {
        return getPercentile(99);
    }

    private long getPercentile(int percentile) {
        if (latencies.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil((percentile / 100.0) * latencies.size()) - 1;
        return latencies.get(Math.max(0, index));
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"sdk\": \"").append(sdk).append("\",\n");
        sb.append("  \"iterations\": ").append(iterations).append(",\n");
        sb.append("  \"totalDurationMs\": ").append(totalDurationMs).append(",\n");
        sb.append("  \"throughput\": ").append(String.format("%.2f", getThroughput())).append(",\n");
        sb.append("  \"latency\": {\n");
        sb.append("    \"min\": ").append(getMinLatency()).append(",\n");
        sb.append("    \"max\": ").append(getMaxLatency()).append(",\n");
        sb.append("    \"mean\": ").append(String.format("%.2f", getMeanLatency())).append(",\n");
        sb.append("    \"p50\": ").append(getP50Latency()).append(",\n");
        sb.append("    \"p90\": ").append(getP90Latency()).append(",\n");
        sb.append("    \"p95\": ").append(getP95Latency()).append(",\n");
        sb.append("    \"p99\": ").append(getP99Latency()).append("\n");
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}
