package com.github.hartsock.toys;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Command(
        name = "Concordance",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands={}
)
public class Concordance implements Runnable{
    @Parameters(paramLabel = "<URL>",
                description = "URL of text to process. Defaults to Frankenstein or, The Modern Prometheus")
    String url;

    List<String> words;
    Map<String, Number> concordance;

    public void setUrl(final String url) {
        this.url = url;
    }

    public void run() {
        System.out.println(url);
        final Wrapper<List<String>, ? extends Throwable> result = getLines(url);
        if(result.hasError()) {
            System.out.println(result.e.getMessage());
            throw new RuntimeException(result.getE());
        }
        final List<String> lines = result.get();

        words = toWords(lines);
        concordance = concordance(words);

        System.out.println();
        System.out.println(lines.get(0));
        if(lines.size() > 1) {
            printConcordance(System.out, concordance);
        }
    }

    void printConcordance(final PrintStream printer, final Map<String, Number> concordance) {
        printer.println("Concordance:");
        for (final String key : concordance.keySet().stream().sorted().collect(Collectors.toList())) {
            printer.println("    " + key + ": " + concordance.get(key));
        }
    }

    static class Wrapper<T,E> {
        final T t;
        final E e;

        Wrapper(final T t, final E e) {
            this.t = t;
            this.e = e;
        }

        T get() {
            return t;
        }

        static <T,E> Wrapper<T,E> ofResult(T t) {
            return new Wrapper(t, null);
        }

        static <T,E> Wrapper<T,E> ofException(E e) {
            return new Wrapper(null, e);
        }

        static <T,E> Wrapper<T,E> of(T t, E e) {
            return new Wrapper(t, e);
        }

        public T orElse(final Function<E, T> f) {
            if(t != null) {
                return t;
            }
            return f.apply(e);
        }

        public boolean hasError() {
            return e != null;
        }

        public E getE() {
            return e;
        }
    }

    Wrapper<List<String>,? extends Throwable> getLines(final String url) {
        try(
                final BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new URL(url).openStream(), "" +
                                "UTF-8"
                        )
                )
        ) {
            return Wrapper.<List<String>, Throwable>ofResult(
                    in.lines().collect(Collectors.toList())
            );
        } catch (final IOException e) {
            return Wrapper.ofException(e);
        }
    }

    List<String> toWords(final List<String> lines) {
        final List<String> words = lines.stream()
                .map((l) -> l.split("(\\W|_)"))
                .flatMap(arr -> Arrays.stream(arr))
                .collect(Collectors.toList());
        return words;
    }

    Map<String, Number> concordance(final List<String> words) {
        final Map<String, Number> concordance = words.stream()
                .filter((w) -> w != null && w.length() > 0)
                .filter((w) -> w.matches("\\w+"))
                .filter((w) -> !w.matches("\\d+"))
                .collect(Collectors.toMap((w) -> w, (w) -> 1, (a, b) -> a.intValue() + b.intValue()));
        return concordance;
    }

    /**
     * return the number of times a word was seen
     * @param word the word to look up
     * @return times seen
     */
    public Number count(final String word) {
        if(concordance == null) {
            throw new IllegalStateException("No text has been loaded yet!");
        }
        if(concordance.containsKey(word)) {
            return concordance.get(word);
        }
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Concordance()).execute(args);
        System.exit(exitCode);
    }
}
