/**
 * ベンチマーク結果クラス
 */
export class BenchmarkResult {
    private sortedLatencies: number[];

    constructor(
        public readonly sdk: string,
        latencies: number[],
        public readonly totalDurationMs: number,
        public readonly iterations: number
    ) {
        this.sortedLatencies = [...latencies].sort((a, b) => a - b);
    }

    getThroughput(): number {
        return (this.iterations * 1000) / this.totalDurationMs;
    }

    getMinLatency(): number {
        return this.sortedLatencies.length > 0 ? this.sortedLatencies[0] : 0;
    }

    getMaxLatency(): number {
        return this.sortedLatencies.length > 0
            ? this.sortedLatencies[this.sortedLatencies.length - 1]
            : 0;
    }

    getMeanLatency(): number {
        if (this.sortedLatencies.length === 0) return 0;
        const sum = this.sortedLatencies.reduce((a, b) => a + b, 0);
        return sum / this.sortedLatencies.length;
    }

    getP50Latency(): number {
        return this.getPercentile(50);
    }

    getP90Latency(): number {
        return this.getPercentile(90);
    }

    getP95Latency(): number {
        return this.getPercentile(95);
    }

    getP99Latency(): number {
        return this.getPercentile(99);
    }

    private getPercentile(percentile: number): number {
        if (this.sortedLatencies.length === 0) return 0;
        const index = Math.max(0, Math.ceil((percentile / 100) * this.sortedLatencies.length) - 1);
        return this.sortedLatencies[index];
    }

    toJson(): string {
        return JSON.stringify({
            sdk: this.sdk,
            iterations: this.iterations,
            totalDurationMs: Math.round(this.totalDurationMs),
            throughput: parseFloat(this.getThroughput().toFixed(2)),
            latency: {
                min: parseFloat(this.getMinLatency().toFixed(2)),
                max: parseFloat(this.getMaxLatency().toFixed(2)),
                mean: parseFloat(this.getMeanLatency().toFixed(2)),
                p50: parseFloat(this.getP50Latency().toFixed(2)),
                p90: parseFloat(this.getP90Latency().toFixed(2)),
                p95: parseFloat(this.getP95Latency().toFixed(2)),
                p99: parseFloat(this.getP99Latency().toFixed(2))
            }
        }, null, 2);
    }
}
