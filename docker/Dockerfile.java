# Zuora SDK Benchmark - Java
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Mavenのインストール
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# プロジェクトファイルをコピー
COPY java/pom.xml .
COPY java/src ./src

# ビルド
RUN mvn clean package -DskipTests

# ランタイムイメージ
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# ビルド成果物をコピー
COPY --from=builder /app/target/*.jar ./app.jar

# 環境変数のデフォルト値
ENV ZUORA_BASE_URL=https://rest.apisandbox.zuora.com
ENV ZUORA_BENCHMARK_ENDPOINT=/v1/catalog/products

# JVMオプション（ベンチマーク用に最適化）
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=100"

# 実行
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
