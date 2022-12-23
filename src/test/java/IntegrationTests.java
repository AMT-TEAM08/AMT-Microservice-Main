import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTests {
    @Test
    void testImageExists() {
        // Given
        String imagePath = "./src/test/resources/car.jpg";
        String maxLabels = "20";
        String minConfidence = "85";
        String[] args = {"-p", imagePath, "-m", maxLabels, "-c", minConfidence};

        // When
        String result = App.processCommand(args);

        // Then
        assertTrue(result.contains("Car"));
    }

    @Test
    void testAlreadyExists() {
        // Given
        String imagePath = "./src/test/resources/car.jpg";
        String maxLabels = "20";
        String minConfidence = "85";
        String[] args = {"-p", imagePath, "-m", maxLabels, "-c", minConfidence};
        App.processCommand(args);

        // When
        String result = App.processCommand(args);

        // Then
        assertTrue(result.contains("Car"));
    }
}