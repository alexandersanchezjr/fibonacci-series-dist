import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fibonacci {
    public static List<Long> fibonacciSequence(long number) {
        return Stream.iterate(new long[] { 1, 1 }, t -> new long[] { t[1], t[0] + t[1] }).limit(number).map(n -> n[0])
                .collect(Collectors.toList());
    }

    public static long fibonacciValue(List<Long> seq) {
        return seq.subList(seq.size() - 2, seq.size()).stream().mapToLong(Long::longValue).sum();
    }
}
