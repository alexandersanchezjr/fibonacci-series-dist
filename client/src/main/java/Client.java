import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import com.zeroc.Ice.Object;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import lombok.SneakyThrows;

public class Client {
    static com.zeroc.Ice.Communicator communicator;

    public static void main(String[] args) {
        communicator = com.zeroc.Ice.Util.initialize(args, "config.client");
        Demo.CallbackSenderPrx server = serverConfiguration();
        Demo.CallbackReceiverPrx client = clientConfiguration();
        if (server == null || client == null)
            throw new Error("Invalid proxy");
        runProgram(server, client);
        communicator.shutdown();
        communicator.destroy();
    }

    @SneakyThrows
    private static void runProgram(Demo.CallbackSenderPrx server, Demo.CallbackReceiverPrx client) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String hostname = InetAddress.getLocalHost().getHostName();
        System.out.print("Welcome please type a number: ");
        String message = br.readLine();
        while (!message.equalsIgnoreCase("exit")) {
            server.initiateCallback(client, hostname + ":" + message);
            System.out.print("Enter another number to calculate: ");
            message = br.readLine();
        }
        server.initiateCallback(client, hostname + ":" + message);
    }

    private static Demo.CallbackReceiverPrx clientConfiguration() {
        ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
        com.zeroc.Ice.Object obj = (Object) new CallbackReceiver();
        ObjectPrx objectPrx = adapter.add(obj, Util.stringToIdentity("callbackReceiver"));
        adapter.activate();
        return Demo.CallbackReceiverPrx.uncheckedCast(objectPrx);
    }

    private static Demo.CallbackSenderPrx serverConfiguration() {
        Demo.CallbackSenderPrx twoway = Demo.CallbackSenderPrx
                .checkedCast(communicator.propertyToProxy("Printer.Proxy")).ice_twoway().ice_secure(false);
        return twoway.ice_twoway();
    }
}