# zuora-sdk-bench

Benchmarks for Zuora SDK comparing Java and Node.js implementations.

## Overview

This repository contains performance benchmarks for Zuora SDK operations, comparing implementations in both Java and Node.js. The benchmarks measure common operations such as JSON parsing, serialization, and data processing to help understand relative performance characteristics.

## Structure

```
zuora-sdk-bench/
├── java/                  # Java benchmarks using JMH
│   ├── pom.xml
│   └── src/main/java/com/zuora/bench/
│       └── ZuoraSdkBenchmark.java
├── node/                  # Node.js benchmarks
│   └── benchmark.js
├── package.json
└── README.md
```

## Requirements

### Java
- Java 11 or higher
- Maven 3.6+

### Node.js
- Node.js 14 or higher
- npm 6+

## Setup

### Install Node.js dependencies
```bash
npm install
```

### Install Java dependencies
```bash
cd java
mvn clean install
```

## Running Benchmarks

### Run Node.js benchmarks
```bash
npm run bench
```

### Run Java benchmarks
```bash
cd java
mvn clean package
java -jar target/benchmarks.jar
```

### Run all benchmarks
```bash
npm run bench:all
```

## Benchmark Operations

Both implementations include benchmarks for:

1. **JSON Parsing** - Deserializing JSON strings to objects
2. **JSON Serialization** - Converting objects to JSON strings
3. **Data Processing** - Basic data manipulation operations

## Results

Benchmark results will show:
- Operations per second
- Average execution time
- Performance comparisons between Java and Node.js

## Contributing

Feel free to add more benchmarks or improve existing ones. Please ensure:
- Both Java and Node.js implementations mirror each other
- Benchmarks are realistic and represent actual SDK operations
- Results are reproducible

## License

MIT