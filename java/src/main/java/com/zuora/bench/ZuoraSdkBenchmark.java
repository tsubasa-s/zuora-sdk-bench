package com.zuora.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark for Zuora SDK operations in Java
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class ZuoraSdkBenchmark {

    private Gson gson;

    @Setup
    public void setup() {
        gson = new Gson();
    }

    @Benchmark
    public String testJsonParsing() {
        String sampleJson = "{\"id\":\"test-123\",\"name\":\"Sample Account\",\"status\":\"Active\"}";
        JsonObject obj = gson.fromJson(sampleJson, JsonObject.class);
        return obj.get("id").getAsString();
    }

    @Benchmark
    public String testJsonSerialization() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", "test-123");
        obj.addProperty("name", "Sample Account");
        obj.addProperty("status", "Active");
        return gson.toJson(obj);
    }

    @Benchmark
    public int testDataProcessing() {
        // Simulate processing of account data
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
        return sum;
    }

    @TearDown
    public void teardown() {
        // Cleanup if needed
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ZuoraSdkBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
