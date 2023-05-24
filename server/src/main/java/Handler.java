import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import Demo.CallbackReceiverPrx;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class Handler {
    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static Handler handler = null;
    private static ExecutorService pool;
    private HashMap<String, CallbackReceiverPrx> clients;
    private Semaphore sem;

    private Handler() {
        this.pool = java.util.concurrent.Executors.newFixedThreadPool(MAX_THREADS);
        this.clients = new HashMap<>();
        this.sem = new Semaphore(1);
    }

    public static Handler getInstance() {
        handler = (handler == null) ? new Handler() : handler;
        return handler;
    }

    public static void execute(Task task) {
        pool.execute(task);
    }

    public void addClient(String message, CallbackReceiverPrx client) {
        String host = message.split(":", 2)[0];
        putClient(host, client);
    }

    @SneakyThrows
    private void putClient(String host, CallbackReceiverPrx client) {
        sem.acquire();
        if (!clients.containsKey(host)) {
            clients.put(host, client);
            System.out.println(String.format("%s joined.", host));
        }
        sem.release();
    }

    @SneakyThrows
    public void removeClient(String host) {
        sem.acquire();
        if (clients.containsKey(host)) {
            clients.remove(host);
            System.out.println(host + " left. \n"); // Debug Porpuses
        }
        sem.release();
    }
}