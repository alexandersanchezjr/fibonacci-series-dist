import java.util.List;
import java.util.concurrent.Semaphore;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import Demo.CallbackReceiverPrx;

@AllArgsConstructor
public class Task implements Runnable {
    private String message;
    private CallbackReceiverPrx proxy;
    private Handler handler;
    private Semaphore sem;

    public Task(String message, CallbackReceiverPrx proxy, Handler handler) {
        this.message = message;
        this.proxy = proxy;
        this.handler = handler;
        this.sem = handler.getSem();
    }

    @Override
    public void run() {
        String host = this.message.split(":", 2)[0];
        String message = this.message.split(":", 2)[1];
        doOption(host, message);
    }

    private void doOption(String host, String message) {
        if (message.startsWith("list client"))
            listClient();
        else if (message.startsWith("to"))
            sendTo(host, message);
        else if (message.startsWith("bc"))
            broadcast(host, message);
        else if (message.startsWith("exit"))
            exit(host);
        else
            proxy.callback(validationLayer(host, message));
    }

    private void listClient() {
        StringBuilder sb = new StringBuilder("Hosts: ");
        sb.append(handler.getClients().keySet().toString());
        proxy.callback(sb.toString());
    }

    @SneakyThrows
    private void sendTo(String host, String message) {
        String to = message.split(":", 2)[0].replace("to", "").trim();
        message = message.split(":", 2)[1];
        sem.acquire();
        if (handler.getClients().containsKey(to))
            handler.getClients().get(to).callback(String.format("%s:%s", host, message));
        else
            handler.getClients().get(host).callback(String.format("%s not found", to));
        sem.release();
    }

    private void broadcast(String host, String message) {

    }

    private void exit(String host) {

    }

    @SneakyThrows
    public String validationLayer(String host, String message) {
        String response = 0 + "";
        if (message.matches("-?\\d+")) {
            System.out.println(message);
        } else {
            long number = Long.parseLong(message);
            if (number > 0) {
                List<Long> seq = Fibonacci.fibonacciSequence(number);
                System.out.println(String.format("%s:%s", host, seq.toString()));
                response = (number != 1 && number != 2) ? Fibonacci.fibonacciValue(seq) + "" : 1 + "";
            }
        }
        return response;
    }
}
