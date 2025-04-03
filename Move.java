public class Move {
    final int divisor;

    public Move(int divisor) {
        if (divisor != 2 && divisor != 3) throw new IllegalArgumentException("Divisor must be 2 or 3");
        this.divisor = divisor;
    }

    public int getDivisor() { return divisor; }

    @Override public String toString() { return "Divide by " + divisor; }
}