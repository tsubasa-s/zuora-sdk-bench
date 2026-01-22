const Benchmark = require('benchmark');

/**
 * Benchmark suite for Zuora SDK operations in Node.js
 */
class ZuoraSdkBenchmark {
    constructor() {
        this.suite = new Benchmark.Suite();
    }

    // JSON Parsing benchmark
    testJsonParsing() {
        const sampleJson = '{"id":"test-123","name":"Sample Account","status":"Active"}';
        const obj = JSON.parse(sampleJson);
        return obj.id;
    }

    // JSON Serialization benchmark
    testJsonSerialization() {
        const obj = {
            id: 'test-123',
            name: 'Sample Account',
            status: 'Active'
        };
        return JSON.stringify(obj);
    }

    // Data Processing benchmark
    testDataProcessing() {
        let sum = 0;
        for (let i = 0; i < 1000; i++) {
            sum += i;
        }
        return sum;
    }

    // Run all benchmarks
    run() {
        console.log('Running Zuora SDK Benchmarks for Node.js\n');

        this.suite
            .add('JSON Parsing', () => {
                this.testJsonParsing();
            })
            .add('JSON Serialization', () => {
                this.testJsonSerialization();
            })
            .add('Data Processing', () => {
                this.testDataProcessing();
            })
            .on('cycle', function(event) {
                console.log(String(event.target));
            })
            .on('complete', function() {
                console.log('\nBenchmarks completed!');
                console.log('Fastest is ' + this.filter('fastest').map('name'));
            })
            .run({ 'async': false });
    }
}

// Run benchmarks if executed directly
if (require.main === module) {
    const benchmark = new ZuoraSdkBenchmark();
    benchmark.run();
}

module.exports = ZuoraSdkBenchmark;
