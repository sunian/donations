import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sun on 1/5/2020.
 * A single Donation. Either Check or Cash
 */
public class Donation implements Comparable<Donation> {

    public static final Pattern CSV_PATTERN = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+),\"?([^\"]+)\"?,\"?(\\$[^\"]+)\"?");
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
     * [line] should be a line from the CSV. Format is date,name,amount
     */
    public Donation(String line, Type type) {
        this.type = type;
        Matcher matcher = CSV_PATTERN.matcher(line);
        boolean found = matcher.find();
        this.month = Integer.parseInt(matcher.group(1));
        this.day = Integer.parseInt(matcher.group(2));
        this.year = Integer.parseInt(matcher.group(3));
        this.name = matcher.group(4);
        this.amount = matcher.group(5);
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