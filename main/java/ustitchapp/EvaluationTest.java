
package ustitchapp;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel; // Initializes JavaFX toolkit
import javafx.scene.image.Image;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.types.TFloat32;
import ustitch.tensormodule.TensorUtils;

import java.io.File;

public class EvaluationTest {

    public static void main(String[] args) throws Exception {
        // Initialize JavaFX (required for Image operations)
        new JFXPanel();

        int numRuns = 3; // repeat each test
        long[] randomTimes = new long[numRuns];
        long[] aiTimes = new long[numRuns];

        // -------------------------
        // 1. Random Pattern Generator
        // -------------------------
        for (int i = 0; i < numRuns; i++) {
            long start = System.currentTimeMillis();
            Image randomPattern = generateRandomPattern();
            long end = System.currentTimeMillis();
            randomTimes[i] = end - start;
        }

        // -------------------------
        // 2. AI Pattern Generator (real TensorFlow)
        // -------------------------
        for (int i = 0; i < numRuns; i++) {
            long start = System.currentTimeMillis();
            Image aiPattern = generateAIPattern();
            long end = System.currentTimeMillis();
            aiTimes[i] = end - start;
        }

        // -------------------------
        // 3. Print results
        // -------------------------
        printResults("Random Patterns", randomTimes);
        printResults("AI Patterns", aiTimes);
    }

    // -------------------------
    // Print results
    // -------------------------
    private static void printResults(String name, long[] times) {
        double avg = average(times);
        double std = stdDeviation(times, avg);
        System.out.println(name + " - Avg: " + avg + " ms, Std Dev: " + std);
    }

    private static double average(long[] arr) {
        double sum = 0;
        for (long t : arr) sum += t;
        return sum / arr.length;
    }

    private static double stdDeviation(long[] arr, double avg) {
        double sum = 0;
        for (long t : arr) sum += Math.pow(t - avg, 2);
        return Math.sqrt(sum / arr.length);
    }

    // -------------------------
    // Pattern generators
    // -------------------------
    private static Image generateRandomPattern() {
        int index = (int) (Math.random() * 10) + 1;
        return new Image(EvaluationTest.class.getResourceAsStream("/images/pattern" + index + ".png"));
    }

    private static Image generateAIPattern() {
        try {
            File baseDir = new File(System.getProperty("user.dir"));
            File modelPath = new File(baseDir, "app/resources/ai_tf2");
            if (!modelPath.exists()) modelPath = new File(baseDir, "resources/ai_tf2");
            if (!modelPath.exists()) throw new RuntimeException("AI model folder not found!");

            try (SavedModelBundle model = SavedModelBundle.load(modelPath.getAbsolutePath(), "serve");
                 Session session = model.session()) {

                Image contentImg = new Image(EvaluationTest.class.getResourceAsStream("/styles/content1.jpg"));
                Image styleImg   = new Image(EvaluationTest.class.getResourceAsStream("/styles/style1.jpg"));

                try (TFloat32 contentTensor = TensorUtils.imageToTensor(contentImg);
                     TFloat32 styleTensor = TensorUtils.imageToTensor(styleImg)) {

                    TFloat32 outputTensor = (TFloat32) session.runner()
                            .feed("serving_default_placeholder", contentTensor)
                            .feed("serving_default_placeholder_1", styleTensor)
                            .fetch("StatefulPartitionedCall")
                            .run().get(0);

                    return TensorUtils.tensorToImage(outputTensor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

