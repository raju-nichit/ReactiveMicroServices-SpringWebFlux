import com.sun.management.OperatingSystemMXBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class StartupResourceLogger implements CommandLineRunner {

    @Override
    public void run(String... args) {
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        System.out.println("Monitoring CPU and Memory usage during startup...");

        // Log CPU/Memory every 1 second for 10 seconds
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            double processCpuLoad = osBean.getProcessCpuLoad();  // 0.0 to 1.0
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

            System.out.printf("CPU Load: %.2f%% | Used Memory: %d MB | Total: %d MB | Max: %d MB%n",
                    processCpuLoad * 100, usedMemory, totalMemory, maxMemory);

        }, 0, 1, TimeUnit.SECONDS);

        // Optional: shut down logging after 10 seconds
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            System.out.println("Finished monitoring startup.");
            System.exit(0); // Or shut down logger if app should continue
        }, 10, TimeUnit.SECONDS);
    }
}
