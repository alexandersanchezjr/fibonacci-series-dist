import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client
{
    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.client",extraArgs))
        {
            com.zeroc.Ice.ObjectPrx base = communicator.propertyToProxy("Printer.Proxy");
            Demo.PrinterPrx printer = Demo.PrinterPrx.checkedCast(base);

            String hostname = java.net.InetAddress.getLocalHost().getHostName();

            if(printer == null)
            {
                throw new Error("Invalid proxy");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int number = 0;
            boolean exit = false;

            do {
                System.out.print("Inserte el numero de la serie fibonacci que quiere calcular: ");
                String response = reader.readLine();
                if (response.equalsIgnoreCase("exit")) {
                    exit = true;
                }else {
                    try {
                        number = Integer.parseInt(response);
                        if (number < 0)
                            System.out.println("Respuesta: " + 0);
                        else 
                            printer.printAnswer(number, hostname);
                    } catch (NumberFormatException e) {
                        System.out.println("El valor ingresado no es un numero");
                    }
                }
            } while (!exit);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}