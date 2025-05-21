import java.util.ArrayList;
import java.util.List;

public class Laiza {
    public static void main(String[] args) {
        List<Integer> lista = new ArrayList<>();

        lista.add(3);
        lista.add(6);

        lista.forEach( System.out::print);
    }
}
