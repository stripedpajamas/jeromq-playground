package jeromq.playground;

import java.util.*;

public class Factor {
  public static Set<Integer> getFactors(int n) {
    Set<Integer> out = new HashSet<Integer>();
    int sqrt = (int) Math.ceil(Math.sqrt((double) n));
    for (int i = 1; i <= sqrt; i++) {
      if (n % i == 0) {
        out.add(i);
        if (i != sqrt) {
          out.add(n / i);
        }
      }
    }
    return out;
  }
}
