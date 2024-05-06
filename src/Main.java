import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        ExecutorService executor = Executors.newFixedThreadPool(25);
        long startTs = System.currentTimeMillis(); // start time
        List<Future<String>> threads = new ArrayList<>();

        for (String text : texts) {
            Callable<String> callable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return text.substring(0, 100) + " -> " + maxSize;
            };
            threads.add(executor.submit(callable));
        }
        executor.shutdown();
        for (Future<String> future : threads) {
            try {
                System.out.println(future.get());  // зависаем, ждём когда поток объект которого лежит в thread завершится
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            long endTs = System.currentTimeMillis(); // end time

            System.out.println("Time: " + (endTs - startTs) + "ms");
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}