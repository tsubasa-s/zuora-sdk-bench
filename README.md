# Zuora SDK Benchmark

Zuora SDKのJavaとNode.jsの性能を比較するためのベンチマークツールです。

Dockerコンテナを使用して同一のリソース制限下で実行することで、公平な性能比較を実現します。

## 構成

```
.
├── docker/
│   ├── Dockerfile.java    # Java環境用Dockerfile
│   └── Dockerfile.node    # Node.js環境用Dockerfile
├── java/                  # Javaベンチマークプロジェクト
│   ├── pom.xml
│   └── src/
├── node/                  # Node.jsベンチマークプロジェクト
│   ├── package.json
│   ├── tsconfig.json
│   └── src/
├── scripts/
│   └── run-benchmark.sh   # ベンチマーク実行スクリプト
├── docker-compose.yml     # Docker Compose設定
└── .env.example           # 環境変数テンプレート
```

## 前提条件

- Docker
- Docker Compose
- Zuora APIの認証情報（Client ID、Client Secret）

## セットアップ

1. リポジトリをクローン

```bash
git clone <repository-url>
cd zuora-sdk-bench
```

2. 環境変数を設定

```bash
cp .env.example .env
# .envファイルを編集してZuora認証情報を設定
```

## 使い方

### 全ベンチマークを実行

```bash
./scripts/run-benchmark.sh
```

### Javaのみ実行

```bash
./scripts/run-benchmark.sh --java-only
```

### Node.jsのみ実行

```bash
./scripts/run-benchmark.sh --node-only
```

### Dockerイメージのビルドのみ

```bash
./scripts/run-benchmark.sh --build-only
```

### 個別にDocker Composeで実行

```bash
# Javaベンチマーク
docker-compose run --rm benchmark-java

# Node.jsベンチマーク
docker-compose run --rm benchmark-node
```

## 環境変数

| 変数名 | 必須 | デフォルト値 | 説明 |
|--------|------|--------------|------|
| `ZUORA_CLIENT_ID` | Yes | - | Zuora OAuth Client ID |
| `ZUORA_CLIENT_SECRET` | Yes | - | Zuora OAuth Client Secret |
| `ZUORA_BASE_URL` | No | `https://rest.apisandbox.zuora.com` | Zuora API Base URL |
| `ZUORA_BENCHMARK_ENDPOINT` | No | `/v1/catalog/products` | ベンチマーク対象のAPIエンドポイント |

## リソース制限

公平な比較のため、両環境に同一のリソース制限を適用しています：

- **CPU**: 2コア（最小1コア予約）
- **メモリ**: 1GB（最小512MB予約）

## ベンチマーク結果

実行結果は `results/` ディレクトリに保存されます：

- `java_YYYYMMDD_HHMMSS.log` - Java実行ログ
- `java_YYYYMMDD_HHMMSS.json` - Java結果（JSON形式）
- `node_YYYYMMDD_HHMMSS.log` - Node.js実行ログ
- `node_YYYYMMDD_HHMMSS.json` - Node.js結果（JSON形式）

### 結果フォーマット（JSON）

```json
{
  "sdk": "java",
  "iterations": 100,
  "totalDurationMs": 12345,
  "throughput": 8.10,
  "latency": {
    "min": 80,
    "max": 250,
    "mean": 123.45,
    "p50": 115,
    "p90": 180,
    "p95": 200,
    "p99": 230
  }
}
```

## 測定項目

- **スループット**: 1秒あたりのリクエスト数（req/sec）
- **レイテンシ統計**:
  - 最小値（Min）
  - 最大値（Max）
  - 平均値（Mean）
  - パーセンタイル（P50、P90、P95、P99）

## カスタマイズ

### ベンチマーク回数の変更

各言語のソースコードで `BENCHMARK_ITERATIONS` 定数を変更してください：

- Java: `java/src/main/java/com/example/benchmark/BenchmarkMain.java`
- Node.js: `node/src/index.ts`

### APIエンドポイントの変更

環境変数 `ZUORA_BENCHMARK_ENDPOINT` で変更できます。
