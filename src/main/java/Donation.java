import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Sun on 1/5/2020.
 * A single Donation. Either Check or Cash
 */
public class Donation implements Comparable<Donation> {

    public static final DecimalFormat CURRENCY_FORMAT = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    static {
        CURRENCY_FORMAT.setParseBigDecimal(true);
    }

    enum Type {
        CHECK, CASH;

        public String getPrefix() {
            return switch (this) {
                case CASH -> "(cash)  ";
                case CHECK -> "";
            };
        }
    }

    final int year, month, day;
    final String name;
    final String amount;
    final Type type;

    public Donation(int year, int month, int day, String name, String amount, Type type) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    /**
     * [line] should be a line from the TSV. Format is date \t name \t amount
     */
    public Donation(String line, Type type) {
        this.type = type;
        final String[] split = line.split("\t");
        if (split.length < 3) {
            throw new IllegalArgumentException("not enough tab separated values:\n" + line);
        }
        final String[] date = split[0].split("/");
        if (date.length != 3) {
            throw new IllegalArgumentException("invalid date:\n" + split[0]);
        }
        this.month = Integer.parseInt(date[0]);
        this.day = Integer.parseInt(date[1]);
        this.year = Integer.parseInt(date[2]);
        this.name = split[1].replace("\\n", "\n");
        this.amount = split[2];
    }

    public BigDecimal getAmount() {
        try {
            return (BigDecimal) CURRENCY_FORMAT.parse(amount);
        } catch (Exception e) {
            System.out.println("failed to parse " + amount);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return String.format("%d/%d/%d,\"%s\",\"%s%s\"", month, day, year, name, type.getPrefix(), amount);
    }

    public String getDate() {
        return String.format("%d/%d/%d", month, day, year);
    }

    public String getCredit() {
        return type.getPrefix() + amount;
    }

    @Override
    public int compareTo(Donation o) {
        return firstNonZero(
                year - o.year,
                month - o.month,
                day - o.day,
                type.ordinal() - o.type.ordinal(),
                amount.compareTo(o.amount),
                name.compareTo(o.name)
        );
    }

    private int firstNonZero(int... args) {
        for (int arg : args) {
            if (arg != 0) {
                return arg;
            }
        }
        return 0;
    }

}