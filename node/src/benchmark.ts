import { BenchmarkConfig } from './config';

/**
 * Zuora APIベンチマーク実行クラス
 *
 * TODO: Zuora SDK が利用可能になったら SDK 経由に置き換える
 * 現在は fetch を使用した直接呼び出しで実装
 */
export class ZuoraBenchmark {
    private accessToken: string | null = null;
    private tokenExpiresAt: number = 0;

    constructor(private readonly config: BenchmarkConfig) {}

    /**
     * 単一のベンチマーク反復を実行
     */
    async runSingleIteration(): Promise<void> {
        // トークンが期限切れまたは未取得の場合は認証
        if (!this.accessToken || Date.now() >= this.tokenExpiresAt) {
            await this.authenticate();
        }

        // APIエンドポイントを呼び出し
        await this.callApi();
    }

    /**
     * OAuth認証でアクセストークンを取得
     */
    private async authenticate(): Promise<void> {
        const credentials = Buffer.from(
            `${this.config.clientId}:${this.config.clientSecret}`
        ).toString('base64');

        const response = await fetch(`${this.config.baseUrl}/oauth/token`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Basic ${credentials}`
            },
            body: 'grant_type=client_credentials'
        });

        if (!response.ok) {
            const body = await response.text();
            throw new Error(`Authentication failed: ${response.status} - ${body}`);
        }

        const data = await response.json() as {
            access_token: string;
            expires_in: number;
        };

        this.accessToken = data.access_token;
        // トークン有効期限を設定（余裕を持って10秒前に期限切れとする）
        this.tokenExpiresAt = Date.now() + (data.expires_in - 10) * 1000;
    }

    /**
     * 指定されたAPIエンドポイントを呼び出し
     */
    private async callApi(): Promise<void> {
        const response = await fetch(`${this.config.baseUrl}${this.config.endpoint}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${this.accessToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const body = await response.text();
            throw new Error(`API call failed: ${response.status} - ${body}`);
        }

        // レスポンスを読み込む（ベンチマークのため）
        await response.json();
    }
}
