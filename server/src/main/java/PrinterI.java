public class PrinterI implements Demo.Printer
{
    public void printAnswer(long l, String hostname, com.zeroc.Ice.Current current)
    {
        
        long result = fib(l);

        System.out.println(hostname + ": " + result);
    }

    private long fib (long n) {
        if (n == 0 || n == 1) {
            return n;
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }
}