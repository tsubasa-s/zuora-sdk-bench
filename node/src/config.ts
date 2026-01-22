/**
 * ベンチマーク設定クラス
 */
export class BenchmarkConfig {
    constructor(
        public readonly clientId: string,
        public readonly clientSecret: string,
        public readonly baseUrl: string,
        public readonly endpoint: string
    ) {}

    static fromEnvironment(): BenchmarkConfig {
        const clientId = process.env.ZUORA_CLIENT_ID || '';
        const clientSecret = process.env.ZUORA_CLIENT_SECRET || '';
        const baseUrl = process.env.ZUORA_BASE_URL || 'https://rest.apisandbox.zuora.com';
        const endpoint = process.env.ZUORA_BENCHMARK_ENDPOINT || '/v1/catalog/products';

        return new BenchmarkConfig(clientId, clientSecret, baseUrl, endpoint);
    }

    isValid(): boolean {
        return this.clientId.length > 0 && this.clientSecret.length > 0;
    }
}
