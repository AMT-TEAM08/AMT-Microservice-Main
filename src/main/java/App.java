import org.apache.commons.cli.*;

import java.util.Arrays;

/**
 * Entry point of our software to detect entities in a picture
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public class App {

    private static void error(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar amt-aws-1.0-SNAPSHOT-jar-with-dependencies.jar <action> <options>...", options);
        System.out.println("\nAvailable actions:");
        Arrays.stream(Action.values()).forEach(x -> System.out.println(x.usage()));
    }

    public static void main(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption("h", "help", false, "Print usage");
        options.addOption("p", "profile", true, "AWS profile name");
        options.addOption("r", "region", true, "AWS region");
        options.addOption("e", "environment", false, "Use environment variables for AWS credentials");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        if (cmd.hasOption("p") && cmd.hasOption("e")) {
            error("Cannot use both profile and environment variables");
        }

        /*AwsServiceConfigurator.Builder builder = new AwsServiceConfigurator.Builder();

        if (cmd.hasOption("p")) {
            builder.withProfile(cmd.getOptionValue("p"));
        } else if (cmd.hasOption("e")) {
            builder.withEnvironmentVariables();
        }

        if (cmd.hasOption("r")) {
            builder.withRegion(cmd.getOptionValue("r"));
        }

        IDataObjectHelper helper = new AWSDataObjectHelperImpl(builder.build());*/

        String[] remainingArgs = cmd.getArgs();

        if (remainingArgs.length == 0) {
            error("No action specified");
        }

        Action action = Action.fromString(remainingArgs[0]);

        if(action == null) {
            error("Unknown action");
        } else {
            try {
                action.execute(helper, Arrays.copyOfRange(remainingArgs, 1, remainingArgs.length));
                System.out.println(action + " executed successfully");
            } catch (Exception e) {
                error("Error while executing " + action + ": " + e.getMessage());
            }
        }
    }
}
