import org.amt.DataObject.Aws.AwsLabelDetectorHelperImpl;
import org.amt.DataObject.Aws.AwsServiceConfigurator;

import java.io.File;
import java.io.IOException;

/**
 * Enum for all possible user actions
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public enum Action {
        ADD("add") {
            @Override
            public void execute(IDataObjectHelper helper, String... args) throws IOException, IllegalArgumentException {
                if (args.length != 2) {
                    throw new IllegalArgumentException("Invalid number of arguments for action " + this.name());
                }
                String key = args[0];
                String file = args[1];

                helper.add(key, new File(file));
            }

            @Override
            public String usage() {
                return "> " + ADD + " <key> <file> - add the <file> to the bucket with the given <key>";
            }
        },
        LIST("list") {
            @Override
            public void execute(IDataObjectHelper helper, String... args) throws IllegalArgumentException {
                if (args.length != 0) {
                    throw new IllegalArgumentException("Invalid number of arguments for action " + this.name());
                }
                System.out.println("--- Objects : ---");
                // print the list of objects adding a - in front of each new line
                helper.listObjects().forEach(x -> System.out.println("- " + x));
            }

            @Override
            public String usage() {
                return "> " + LIST + " - list all files in the bucket";
            }
        },
        GET("get") {
            @Override
            public void execute(IDataObjectHelper helper, String... args) throws IllegalArgumentException {
                if (args.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments for action " + this.name());
                }
                String key = args[0];
                System.out.println("Url to " + key + " : " + helper.getUrl(key));
            }

            @Override
            public String usage() {
                return "> " + GET + " <key> - get a url to a given <key>";
            }
        },
        DELETE("delete") {
            @Override
            public void execute(IDataObjectHelper helper, String... args) throws IllegalArgumentException {
                if (args.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments for action " + this.name());
                }
                String key = args[0];

                helper.delete(key);
            }

            @Override
            public String usage() {
                return "> " + DELETE + " <key> - delete the file with the given <key>";
            }
        },
        RECOGNIZE("recognize") {
            @Override
            public void execute(IDataObjectHelper helper, String... args) throws Exception {
                if (args.length != 2) {
                    throw new IllegalArgumentException("Invalid number of arguments for action " + this.name());
                }
                String key = args[0];
                int nbLabels = Integer.parseInt(args[1]);
                ILabelDetector recognizer = new AwsLabelDetectorHelperImpl(new AwsServiceConfigurator.Builder().build());
                System.out.println("Labels : " + recognizer.detectLabels(helper.getName(), key, nbLabels).getLabels());
            }

            @Override
            public String usage() {
                return "> " + RECOGNIZE + " <key> <nbLabels> - detect <nbLabels> labels in the <key> image";
            }
        };

        private final String stringValue;

        Action(String stringValue) {
            this.stringValue = stringValue;
        }
        public String toString() {
            return stringValue;
        }

        public static Action fromString(String text) {
            for (Action b : Action.values()) {
                if (b.stringValue.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        public void execute(IDataObjectHelper helper, String... args) throws Exception {
            throw new UnsupportedOperationException();
        }

        public String usage() {
            return "No help available";
        }
}