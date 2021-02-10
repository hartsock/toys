package com.github.hartsock.toys;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void run() {
        System.out.println(url);
        final List<String> lines = new LinkedList<>();
        try {
            final URL textUrl = new URL(url);
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(textUrl.openStream(), "UTF-8"));
            String line = in.readLine();
            while(line != null) {
                lines.add(line);
                line = in.readLine();
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getCause());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        words = toWords(lines);
        concordance = concordance(words);

        System.out.println();
        System.out.println(lines.get(0));
        System.out.println("Concordance:");
        for (final String key : concordance.keySet().stream().sorted().collect(Collectors.toList())) {
            System.out.println("    " + key + ": " + concordance.get(key));
        }
    }

    Map<String, Number> concordance(final List<String> words) {
        final Map<String, Number> concordance = words.stream()
                .filter((w) -> w != null && w.length() > 0)
                .filter((w) -> w.matches("\\w+"))
                .filter((w) -> !w.matches("\\d+"))
                .collect(Collectors.toMap((w) -> w, (w) -> 1, (a, b) -> a.intValue() + b.intValue()));
        return concordance;
    }

    List<String> toWords(final List<String> lines) {
        final List<String> words = lines.stream()
                .map((l) -> l.split("(\\W|_)"))
                .flatMap(arr -> Arrays.stream(arr))
                .collect(Collectors.toList());
        return words;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Concordance()).execute(args);
        System.exit(exitCode);
    }

    public Number count(final String word) {
        if(concordance == null) {
            return -1;
        }
        if(concordance.containsKey(word)) {
            return concordance.get(word);
        }
        return 0;
    }
}
