import com.zeroc.Ice.Current;
import Demo.CallbackReceiverPrx;

public class CallbackSender implements Demo.CallbackSender {

    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {
        Task task = new Task(message, proxy, Handler.getInstance());
        Handler.getInstance().addClient(message, proxy);
        Handler.execute(task);
    }
}