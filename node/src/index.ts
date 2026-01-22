import { BenchmarkConfig } from './config';
import { ZuoraBenchmark } from './benchmark';
import { BenchmarkResult } from './result';

const WARMUP_ITERATIONS = 5;
const BENCHMARK_ITERATIONS = 100;

async function main(): Promise<void> {
    console.log('=== Zuora SDK Benchmark - Node.js ===');
    console.log();

    // 環境情報の表示
    printEnvironmentInfo();

    // 設定の読み込み
    const config = BenchmarkConfig.fromEnvironment();

    if (!config.isValid()) {
        console.error('Error: Required environment variables not set.');
        console.error('Please set ZUORA_CLIENT_ID and ZUORA_CLIENT_SECRET');
        process.exit(1);
    }

    // ベンチマーク実行
    const benchmark = new ZuoraBenchmark(config);

    try {
        // ウォームアップ
        console.log('--- Warmup Phase ---');
        await runWarmup(benchmark);

        // ベンチマーク実行
        console.log();
        console.log('--- Benchmark Phase ---');
        const result = await runBenchmark(benchmark);

        // 結果出力
        console.log();
        printResults(result);

        // JSON形式でも出力（他ツールでの解析用）
        console.log();
        console.log('--- JSON Output ---');
        console.log(result.toJson());

    } catch (error) {
        console.error('Benchmark failed:', error);
        process.exit(1);
    }
}

function printEnvironmentInfo(): void {
    console.log('Environment Information:');
    console.log(`  Node.js Version: ${process.version}`);
    console.log(`  Platform: ${process.platform}`);
    console.log(`  Arch: ${process.arch}`);
    console.log(`  Available CPUs: ${require('os').cpus().length}`);
    console.log(`  Total Memory: ${Math.round(require('os').totalmem() / 1024 / 1024)} MB`);
    console.log();
}

async function runWarmup(benchmark: ZuoraBenchmark): Promise<void> {
    console.log(`Running ${WARMUP_ITERATIONS} warmup iterations...`);
    for (let i = 0; i < WARMUP_ITERATIONS; i++) {
        await benchmark.runSingleIteration();
        process.stdout.write('.');
    }
    console.log(' Done');
}

async function runBenchmark(benchmark: ZuoraBenchmark): Promise<BenchmarkResult> {
    console.log(`Running ${BENCHMARK_ITERATIONS} benchmark iterations...`);

    const latencies: number[] = [];
    const totalStartTime = process.hrtime.bigint();

    for (let i = 0; i < BENCHMARK_ITERATIONS; i++) {
        const start = process.hrtime.bigint();
        await benchmark.runSingleIteration();
        const end = process.hrtime.bigint();

        const latencyMs = Number(end - start) / 1_000_000;
        latencies.push(latencyMs);

        if ((i + 1) % 10 === 0) {
            console.log(`  Completed ${i + 1}/${BENCHMARK_ITERATIONS} iterations`);
        }
    }

    const totalEndTime = process.hrtime.bigint();
    const totalDurationMs = Number(totalEndTime - totalStartTime) / 1_000_000;

    return new BenchmarkResult('node', latencies, totalDurationMs, BENCHMARK_ITERATIONS);
}

function printResults(result: BenchmarkResult): void {
    console.log('=== Benchmark Results ===');
    console.log(`  Total Iterations: ${result.iterations}`);
    console.log(`  Total Duration: ${result.totalDurationMs.toFixed(0)} ms`);
    console.log(`  Throughput: ${result.getThroughput().toFixed(2)} req/sec`);
    console.log();
    console.log('  Latency Statistics (ms):');
    console.log(`    Min: ${result.getMinLatency().toFixed(2)}`);
    console.log(`    Max: ${result.getMaxLatency().toFixed(2)}`);
    console.log(`    Mean: ${result.getMeanLatency().toFixed(2)}`);
    console.log(`    P50: ${result.getP50Latency().toFixed(2)}`);
    console.log(`    P90: ${result.getP90Latency().toFixed(2)}`);
    console.log(`    P95: ${result.getP95Latency().toFixed(2)}`);
    console.log(`    P99: ${result.getP99Latency().toFixed(2)}`);
}

main();
