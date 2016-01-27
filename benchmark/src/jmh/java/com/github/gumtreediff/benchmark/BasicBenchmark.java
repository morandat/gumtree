package com.github.gumtreediff.benchmark;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;


@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
public class BasicBenchmark {

    @State(Scope.Benchmark)
    public static class FileNames {
        @Param({
                "/Users/morandat/work/src/gumtree/benchmark/build/resources/jmh/test_v0.java",
                "/Users/morandat/work/src/gumtree/benchmark/build/resources/jmh/test_v0.java"
        })
        volatile String srcName;

        volatile ITree src;
        volatile ITree dst;

        @Setup
        public void loadTrees() throws IOException {
            src = new JdtTreeGenerator().generateFromFile(srcName).getRoot();
            dst = new JdtTreeGenerator().generateFromFile(otherName(srcName)).getRoot();
        }
    }

    public static final String otherName(String name) {
        return name.replace("_v0.java", "_v1.java");
    }

    @Benchmark
    public void ParsingFromDisk(FileNames state, Blackhole bh) throws IOException {
            Matcher matcher = new CompositeMatchers.ClassicGumtree(
                    new JdtTreeGenerator().generateFromFile(state.srcName).getRoot(),
                    new JdtTreeGenerator().generateFromFile(otherName(state.srcName)).getRoot(),
                    new MappingStore());
            bh.consume(matcher);
    }

    @Benchmark
    public void GumTreeClassicFromDisk(FileNames state) throws IOException {
        Matcher matcher = new CompositeMatchers.ClassicGumtree(
                new JdtTreeGenerator().generateFromFile(state.srcName).getRoot(),
                new JdtTreeGenerator().generateFromFile(otherName(state.srcName)).getRoot(),
                new MappingStore());
        matcher.match();
    }

    @Benchmark
    public void GumTreeClassicFromMemory(FileNames state) throws IOException {
        Matcher matcher = new CompositeMatchers.ClassicGumtree(
                state.src,
                state.dst,
                new MappingStore());
        matcher.match();
    }
}
