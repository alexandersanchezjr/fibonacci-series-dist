
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.math.BigInteger;

import com.zeroc.Ice.Current;

import Demo.CallbackReceiverPrx;

public class CallbackSender implements Demo.CallbackSender {

    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {
        proxy.callback(validationLayer(message));
    }

    public String validationLayer(String message) {
        String response = 0 + "";
        String print = message;
        try {
            BigInteger number = new BigInteger (message.split(":", 2)[1]);
            if (number.compareTo(BigInteger.valueOf(0)) == 1) {
                List<BigInteger> seq = fibonacciSequence(number);
                print = message.split(":", 2)[0] + ":" + seq.toString();
                response = (!number.equals(1) && !number.equals(2)) ? fibonacciValue(seq) + "" : 1 + "";
            }
        } catch (Exception e) {}
        System.out.println(print);
        return response;
    }

    public List<BigInteger> fibonacciSequence(BigInteger number) {
        return Stream.iterate(new BigInteger[] { new BigInteger("1"), new BigInteger("1") }, t -> new BigInteger[] { t[1], t[0].add(t[1]) }).limit(number.longValue()).map(n -> n[0])
                .collect(Collectors.toList());
    }

    public BigInteger fibonacciValue(List<BigInteger> seq) {
        return seq.subList(seq.size() - 3, seq.size() - 1).stream().reduce(BigInteger.ZERO, BigInteger::add);
    }

}