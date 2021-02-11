package com.github.hartsock.toys;

import com.github.hartsock.toys.cmds.Concordance;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * This main class doesn't do much but hold references to the other classes.
 * It's job is to act as a central entry point to the other commands we
 * will add over time.
 * <p/>
 * Each toy could have been rolled as a stand alone but done this way
 * we can keep a collection of toys with common utilities together.
 * <p/>
 * Each toy is really about playing with different Java programming
 * features, utilities, syntax, and Java API.
 * <p/>
 * @author hartsock
 */
@CommandLine.Command(
        name = "toys",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands={
                Concordance.class
        }
)
public class Main implements Runnable {
        public static final String HEADER_PROPERTY_NAME = "hartsock.toys.header";
        public static final String MESSAGES_PROPERTIES = "messages.properties";
        private final Properties properties;

        public Main(final Properties properties) {
                this.properties = properties;
        }

        public String message(final String key) {
                return properties.getProperty(key, "[" + key + "]");
        }

        @Override
        public void run() {
                System.out.println(message(HEADER_PROPERTY_NAME));
                CommandLine.usage(this, System.out);
        }

        private static Properties getProperties() {
                final Properties properties = new Properties();
                try (final InputStream stream =
                             Main.class.getClassLoader().getResourceAsStream(MESSAGES_PROPERTIES)) {
                        properties.load(stream);
                } catch (IOException e) {
                        System.out.println(e.getMessage());
                }
                return properties;
        }

        public static void main(String[] args) {
                final Properties properties = getProperties();
                int exitCode = new CommandLine(new Main(properties))
                        .execute(args);
                System.exit(exitCode);
        }
}
