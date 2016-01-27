package com.github.gumtreediff.benchmark;

import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.TreeContext;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

public class SimpleBenchmark {

    @State(Scope.Thread)
    public static class ThreadState {
        volatile Matcher matcher;

        @Setup
        public void initMatcher() throws IOException {
            matcher = new CompositeMatchers.ClassicGumtree(
                    Generators.getInstance().getTree("/test_v0.java").getRoot(),
                    Generators.getInstance().getTree("/test_v1.java").getRoot(),
                    new MappingStore()
            );
        }
    }
}
