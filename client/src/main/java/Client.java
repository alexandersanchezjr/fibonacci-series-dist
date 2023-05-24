import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import java.math.BigInteger;

public class Client {
    static com.zeroc.Ice.Communicator communicator;

    public static void main(String[] args) {
        BigInteger inputNumber = new BigInteger(args[0]);
        java.util.List<String> extraArgs = new java.util.ArrayList<>();
        communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs);
        Demo.CallbackSenderPrx server = serverConfiguration();
        Demo.CallbackReceiverPrx client = clientConfiguration();
        if (server == null || client == null)
            throw new Error("Invalid proxy");
        runProgram(server, client, inputNumber);
        communicator.shutdown();
        communicator.destroy();
    }

    /**
     * 
     * @param server
     * @param client
     */
    public static void runProgram(CallbackSenderPrx server, CallbackReceiverPrx client, /* String message, */ BigInteger inputNumber) {
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();;
            System.out.println(hostname + " - Welcome please type a number 🫰: " + inputNumber);
            server.initiateCallback(client, hostname + ":" + inputNumber);
            /* String message = br.readLine();
            while (!message.equalsIgnoreCase("exit")) {
                server.initiateCallback(client, hostname + ":" + inputNumber);
                System.out.print("Enter another number to calculate ⭐️: ");
                message =  br.readLine();
            }
            */
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method responsible for client callback creation
     * 
     * @return
     */
    public static Demo.CallbackReceiverPrx clientConfiguration() {
        ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
        com.zeroc.Ice.Object obj = new CallbackReceiver();
        ObjectPrx objectPrx = adapter.add(obj, Util.stringToIdentity("callbackReceiver"));
        adapter.activate();
        return Demo.CallbackReceiverPrx.uncheckedCast(objectPrx);
    }

    /**
     * Method responsible for server callback creation
     * 
     * @return
     */
    public static Demo.CallbackSenderPrx serverConfiguration() {
        Demo.CallbackSenderPrx twoway = Demo.CallbackSenderPrx
                .checkedCast(communicator.propertyToProxy("Printer.Proxy")).ice_twoway().ice_secure(false);
        return twoway.ice_twoway();
    }

}