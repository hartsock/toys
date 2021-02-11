package com.github.hartsock.toys;

import com.github.hartsock.toys.cmds.Concordance;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

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
