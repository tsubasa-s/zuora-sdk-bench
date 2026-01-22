#!/bin/bash

# Zuora SDK Benchmark 実行スクリプト
# JavaとNode.jsの両方でベンチマークを実行し、結果を比較

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# 色付き出力
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Zuora SDK Benchmark Runner${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# 環境変数チェック
if [ -z "$ZUORA_CLIENT_ID" ] || [ -z "$ZUORA_CLIENT_SECRET" ]; then
    if [ -f .env ]; then
        echo -e "${YELLOW}Loading environment variables from .env file...${NC}"
        export $(grep -v '^#' .env | xargs)
    else
        echo -e "${RED}Error: ZUORA_CLIENT_ID and ZUORA_CLIENT_SECRET must be set.${NC}"
        echo -e "${YELLOW}Please create a .env file or export the environment variables.${NC}"
        echo "Example:"
        echo "  export ZUORA_CLIENT_ID=your_client_id"
        echo "  export ZUORA_CLIENT_SECRET=your_client_secret"
        exit 1
    fi
fi

# 結果ディレクトリ作成
RESULTS_DIR="$PROJECT_DIR/results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
mkdir -p "$RESULTS_DIR"

# コマンドライン引数の処理
RUN_JAVA=true
RUN_NODE=true
BUILD_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --java-only)
            RUN_NODE=false
            shift
            ;;
        --node-only)
            RUN_JAVA=false
            shift
            ;;
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo
            echo "Options:"
            echo "  --java-only    Run only Java benchmark"
            echo "  --node-only    Run only Node.js benchmark"
            echo "  --build-only   Only build Docker images, don't run benchmarks"
            echo "  --help         Show this help message"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            exit 1
            ;;
    esac
done

# Dockerイメージのビルド
echo -e "${GREEN}Building Docker images...${NC}"

if [ "$RUN_JAVA" = true ]; then
    echo -e "${YELLOW}Building Java image...${NC}"
    docker-compose build benchmark-java
fi

if [ "$RUN_NODE" = true ]; then
    echo -e "${YELLOW}Building Node.js image...${NC}"
    docker-compose build benchmark-node
fi

if [ "$BUILD_ONLY" = true ]; then
    echo -e "${GREEN}Build completed successfully!${NC}"
    exit 0
fi

echo
echo -e "${GREEN}Starting benchmarks...${NC}"
echo

# Javaベンチマーク実行
if [ "$RUN_JAVA" = true ]; then
    echo -e "${BLUE}----------------------------------------${NC}"
    echo -e "${BLUE}Running Java Benchmark...${NC}"
    echo -e "${BLUE}----------------------------------------${NC}"

    JAVA_OUTPUT="$RESULTS_DIR/java_${TIMESTAMP}.log"
    docker-compose run --rm benchmark-java 2>&1 | tee "$JAVA_OUTPUT"

    # JSON結果を抽出
    grep -A 20 "JSON Output" "$JAVA_OUTPUT" | tail -n +2 > "$RESULTS_DIR/java_${TIMESTAMP}.json" 2>/dev/null || true

    echo
fi

# Node.jsベンチマーク実行
if [ "$RUN_NODE" = true ]; then
    echo -e "${BLUE}----------------------------------------${NC}"
    echo -e "${BLUE}Running Node.js Benchmark...${NC}"
    echo -e "${BLUE}----------------------------------------${NC}"

    NODE_OUTPUT="$RESULTS_DIR/node_${TIMESTAMP}.log"
    docker-compose run --rm benchmark-node 2>&1 | tee "$NODE_OUTPUT"

    # JSON結果を抽出
    grep -A 20 "JSON Output" "$NODE_OUTPUT" | tail -n +2 > "$RESULTS_DIR/node_${TIMESTAMP}.json" 2>/dev/null || true

    echo
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Benchmark completed!${NC}"
echo -e "${GREEN}========================================${NC}"
echo
echo -e "Results saved to: ${YELLOW}$RESULTS_DIR${NC}"
echo "  - java_${TIMESTAMP}.log"
echo "  - java_${TIMESTAMP}.json"
echo "  - node_${TIMESTAMP}.log"
echo "  - node_${TIMESTAMP}.json"
