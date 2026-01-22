package com.example.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

/**
 * Zuora SDK Benchmark - Java
 *
 * ベンチマーク実行用メインクラス
 */
public class BenchmarkMain {

    private static final int WARMUP_ITERATIONS = 5;
    private static final int BENCHMARK_ITERATIONS = 100;

    public static void main(String[] args) {
        System.out.println("=== Zuora SDK Benchmark - Java ===");
        System.out.println();

        // 環境情報の表示
        printEnvironmentInfo();

        // 設定の読み込み
        BenchmarkConfig config = BenchmarkConfig.fromEnvironment();

        if (!config.isValid()) {
            System.err.println("Error: Required environment variables not set.");
            System.err.println("Please set ZUORA_CLIENT_ID and ZUORA_CLIENT_SECRET");
            System.exit(1);
        }

        // ベンチマーク実行
        ZuoraBenchmark benchmark = new ZuoraBenchmark(config);

        try {
            // ウォームアップ
            System.out.println("--- Warmup Phase ---");
            runWarmup(benchmark);

            // ベンチマーク実行
            System.out.println();
            System.out.println("--- Benchmark Phase ---");
            BenchmarkResult result = runBenchmark(benchmark);

            // 結果出力
            System.out.println();
            printResults(result);

            // JSON形式でも出力（他ツールでの解析用）
            System.out.println();
            System.out.println("--- JSON Output ---");
            System.out.println(result.toJson());

        } catch (Exception e) {
            System.err.println("Benchmark failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printEnvironmentInfo() {
        System.out.println("Environment Information:");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("  Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("  Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("  Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        System.out.println();
    }

    private static void runWarmup(ZuoraBenchmark benchmark) throws Exception {
        System.out.println("Running " + WARMUP_ITERATIONS + " warmup iterations...");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            benchmark.runSingleIteration();
            System.out.print(".");
        }
        System.out.println(" Done");
    }

    private static BenchmarkResult runBenchmark(ZuoraBenchmark benchmark) throws Exception {
        System.out.println("Running " + BENCHMARK_ITERATIONS + " benchmark iterations...");

        List<Long> latencies = new ArrayList<>();
        long totalStartTime = System.nanoTime();

        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            Instant start = Instant.now();
            benchmark.runSingleIteration();
            Instant end = Instant.now();

            long latencyMs = Duration.between(start, end).toMillis();
            latencies.add(latencyMs);

            if ((i + 1) % 10 == 0) {
                System.out.println("  Completed " + (i + 1) + "/" + BENCHMARK_ITERATIONS + " iterations");
            }
        }

        long totalEndTime = System.nanoTime();
        long totalDurationMs = (totalEndTime - totalStartTime) / 1_000_000;

        return new BenchmarkResult("java", latencies, totalDurationMs, BENCHMARK_ITERATIONS);
    }

    private static void printResults(BenchmarkResult result) {
        System.out.println("=== Benchmark Results ===");
        System.out.println("  Total Iterations: " + result.getIterations());
        System.out.println("  Total Duration: " + result.getTotalDurationMs() + " ms");
        System.out.println("  Throughput: " + String.format("%.2f", result.getThroughput()) + " req/sec");
        System.out.println();
        System.out.println("  Latency Statistics (ms):");
        System.out.println("    Min: " + result.getMinLatency());
        System.out.println("    Max: " + result.getMaxLatency());
        System.out.println("    Mean: " + String.format("%.2f", result.getMeanLatency()));
        System.out.println("    P50: " + result.getP50Latency());
        System.out.println("    P90: " + result.getP90Latency());
        System.out.println("    P95: " + result.getP95Latency());
        System.out.println("    P99: " + result.getP99Latency());
    }
}
