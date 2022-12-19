import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.List;

/**
 * Entry point of our software to detect entities in a picture
 * @author De Bleser Dimitri
 * @author Peer Vincent
 * @author Nelson Jeanreneaud
 */
public class App {

    private static final String DATA_OBJECT_MICROSERVICE_URL = "http://localhost:8080";
    private static final String LABEL_DETECTION_MICROSERVICE_URL = "http://localhost:8081";
    private static final String DEFAULT_MAX_LABELS = "10";
    private static final String DEFAULT_MIN_CONFIDENCE = "0.5";

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar amt-aws-1.0-SNAPSHOT-jar-with-dependencies.jar <options>...", options);
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "Print usage");
        options.addRequiredOption("p", "path", true, "Path to the file to upload");
        options.addOption("c", "confidence", true, "Confidence threshold for the label detection");
        options.addOption("m", "max", true, "Maximum number of labels to return");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printHelp(options);
                return;
            }

            String path = cmd.getOptionValue("p");

            File file = new File(path);
            if (!file.exists()) {
                System.err.println("File does not exist");
                System.exit(1);
            }

            String confidence = cmd.getOptionValue("c", DEFAULT_MIN_CONFIDENCE);
            String maxLabels = cmd.getOptionValue("m", DEFAULT_MAX_LABELS);

            HttpClient client = HttpClients.createDefault();

            // Post object
            String fileName = RandomStringUtils.randomAlphanumeric(10);

            HttpPost post = new HttpPost(DATA_OBJECT_MICROSERVICE_URL + "/objects");
            post.setEntity(MultipartEntityBuilder.create()
                    .addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, fileName)
                    .build());

            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Error while uploading file");
                System.exit(1);
            }

            // GET object

            HttpGet get = new HttpGet(DATA_OBJECT_MICROSERVICE_URL + "/objects/" + fileName);
            response = client.execute(get);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Error while getting file URL");
                System.exit(1);
            }
            InputStream imageURL = response.getEntity().getContent();

            String imageURLString = readInputStream(imageURL);

            System.out.println("Image URL: " + imageURLString);

            // POST label detection
            HttpPost postLabel = new HttpPost(LABEL_DETECTION_MICROSERVICE_URL + "/labels");

            postLabel.setEntity(new UrlEncodedFormEntity(List.of(
                    new BasicNameValuePair("imageURL", imageURLString),
                    new BasicNameValuePair("confidence", confidence),
                    new BasicNameValuePair("maxLabels", maxLabels)
            )));

            response = client.execute(postLabel);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Error while getting labels");
                System.exit(1);
            }

            InputStream labels = response.getEntity().getContent();

            String labelsString = readInputStream(labels);

            System.out.println("Labels: " + labelsString);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
