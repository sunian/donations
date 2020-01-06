import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Sun on 1/5/2020.
 * Consumes CSVs and outputs a CSV for each person
 */
public class ReportGenerator {

    /**
     * maps person name to a list of their donations
     */
    private final Map<String, List<Donation>> donationsByName = new HashMap<>();
    /**
     * what year we are reporting on
     */
    private final int year;

    public ReportGenerator(int year) {
        this.year = year;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the desired year: ");
        ReportGenerator reportGenerator = new ReportGenerator(scanner.nextInt());
        reportGenerator.addDonationsFromCSV("donations - check GFCC.csv", Donation.Type.CHECK);
        reportGenerator.addDonationsFromCSV("donations - cash GFCC.csv", Donation.Type.CASH);
        reportGenerator.outputReport();
    }

    private void outputReport() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("report_" + year + ".csv"));
            List<String> names = new ArrayList<>(donationsByName.keySet());
            Collections.sort(names);
            for (String name : names) {
                String report = getReportFor(name);
                writer.write(report);
                writer.write(",,,\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getReportFor(String name) {
        StringBuilder sb = new StringBuilder();
        List<Donation> donations = donationsByName.get(name);
        Collections.sort(donations);
        BigDecimal total = BigDecimal.ZERO;
        for (Donation donation : donations) {
            total = total.add(donation.getAmount());
            sb.append(donation);
            sb.append('\n');
        }
        sb.append(String.format("Total,,\"%s\"\n", Donation.CURRENCY_FORMAT.format(total)));
        return sb.toString();
    }

    private void addDonationsFromCSV(String fileName, Donation.Type type) {
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream("resources/" + fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = bufferedReader.readLine();
            if (!firstLine.equals("Date,Name,Credit")) {
                throw new RuntimeException("first line of CSV '" + fileName + "' should start with column names: Date,Name,Credit");
            }
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                Donation donation = new Donation(line, type);
                if (donation.year != this.year) {
                    continue;
                }
                List<Donation> donations = donationsByName.get(donation.name);
                if (donations == null) {
                    donations = new ArrayList<>();
                }
                donations.add(donation);
                donationsByName.put(donation.name, donations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}