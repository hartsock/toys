package com.github.hartsock.toys.cmds;

import com.github.hartsock.toys.ResultHolder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(
        name = "concordance",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands={}
)
public class Concordance implements Runnable, Callable<Map<String,Number>> {
    @Parameters(paramLabel = "<URL>",
                description = "URL of text to process")
    private String url;

    List<String> lines;
    List<String> words;
    Map<String, Number> concordance;

    public Concordance(final String url) {
        this.url = url;
    }

    public Concordance() {
        this(null);
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void run() {
        System.out.println(url);

        try {
            call();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            throw new CommandRuntimeException(e);
        }

        System.out.println();
        System.out.println(lines.get(0));
        if(lines.size() > 1) {
            printConcordance(System.out, concordance);
        }
    }

    @Override
    public Map<String, Number> call() throws Exception {
        final ResultHolder<List<String>, ? extends Throwable> result = getLines(url);
        if(result.hasError()) {
            System.out.println(result.getE().getMessage());
            throw new CommandRuntimeException(result.getE());
        }
        lines = result.get();

        words = toWords(lines);
        concordance = concordance(words);
        return concordance;
    }

    public static void printConcordance(final PrintStream printer, final Map<String, Number> concordance) {
        printer.println("Concordance:");
        for (final String key : concordance.keySet().stream().sorted().collect(Collectors.toList())) {
            printer.println("    " + key + ": " + concordance.get(key));
        }
    }
    
    ResultHolder<List<String>,? extends Throwable> getLines(final String url) {
        try(
                final BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new URL(url).openStream(), "UTF-8"
                        )
                )
        ) {
            return ResultHolder.<List<String>, Throwable>ofResult(
                    in.lines().collect(Collectors.toList())
            );
        } catch (final IOException e) {
            return ResultHolder.ofException(e);
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
