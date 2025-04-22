/*
 * DaycareApp.java
 * Main driver to run the daycare system using StaffMember and Child classes.
 */
import java.util.Scanner;

public class DayCareApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final int MAX = 100;
        final int CURRENT_YEAR = 2024;
        final double TAX_RATE = 0.05;

        // 1) Prompt for counts
        System.out.print("Enter number of parents providing ratings (max 100): ");
        int parentCount = Math.min(scanner.nextInt(), MAX);
        System.out.print("Enter number of staff members (max 100): ");
        int staffCount  = Math.min(scanner.nextInt(), MAX);
        System.out.print("Enter number of children (max 100): ");
        int childCount  = Math.min(scanner.nextInt(), MAX);
        scanner.nextLine();

        // 2) Instantiate arrays of objects and ratings matrix
        StaffMember[] staff   = new StaffMember[staffCount];
        Child[]       kids    = new Child[childCount];
        double[][]    ratings = new double[parentCount][staffCount];

        // 3) Input staff information
        System.out.println("\n--- Enter Staff Information ---");
        for (int i = 0; i < staffCount; i++) {
            System.out.printf("Staff #%d Name: ", i+1);
            String name = scanner.nextLine();
            System.out.print("Year of Birth: ");
            int by = scanner.nextInt(); scanner.nextLine();
            System.out.print("Gender: ");
            String g = scanner.nextLine();
            System.out.print("Job Title: ");
            String jt = scanner.nextLine();
            System.out.print("Weekly Hours: ");
            int wh = scanner.nextInt();
            System.out.print("Wage Rate: ");
            double wr = scanner.nextDouble();
            System.out.print("Hiring Year: ");
            int hy = scanner.nextInt(); scanner.nextLine();

            staff[i] = new StaffMember(name, by, g, jt, wh, wr, hy);
            System.out.println();
        }

        // 4) Input children information
        System.out.println("--- Enter Children Information ---");
        for (int i = 0; i < childCount; i++) {
            System.out.printf("Child #%d Name: ", i+1);
            String name = scanner.nextLine();
            System.out.print("Year of Birth: ");
            int by = scanner.nextInt(); scanner.nextLine();
            System.out.print("Gender: ");
            String g = scanner.nextLine();
            System.out.print("Parent Name: ");
            String pn = scanner.nextLine();
            System.out.print("Parent Phone: ");
            String pp = scanner.nextLine();
            System.out.print("Parent Language: ");
            String pl = scanner.nextLine();
            System.out.print("Allergies (true/false): ");
            boolean a = scanner.nextBoolean();
            System.out.print("Days/Week: ");
            int d = scanner.nextInt();
            System.out.print("Shift (1=AM,2=PM,3=Full): ");
            int s = scanner.nextInt(); scanner.nextLine();
            System.out.print("Drop Off Time: ");
            String dt = scanner.nextLine();
            System.out.print("Pick Up Time: ");
            String pt = scanner.nextLine();

            kids[i] = new Child(name, by, g, pn, pp, pl, a, d, s, dt, pt);
            System.out.println();
        }

        // 5) Input parent ratings
        System.out.println("--- Parent Ratings for Staff ---");
        for (int p = 0; p < parentCount; p++) {
            System.out.printf("Parent #%d:%n", p+1);
            for (int s = 0; s < staffCount; s++) {
                System.out.printf("  %s: ", staff[s].getName());
                ratings[p][s] = scanner.nextDouble();
            }
            scanner.nextLine();
            System.out.println();
        }

        // 6) Compute average ratings
        for (int s = 0; s < staffCount; s++) {
            double sum = 0;
            for (int p = 0; p < parentCount; p++) sum += ratings[p][s];
            staff[s].setAverageRating(sum / parentCount);
        }

        // 7) Sort staff by average rating (descending)
        for (int pass = 0; pass < staffCount - 1; pass++) {
            for (int j = 0; j < staffCount - pass - 1; j++) {
                if (staff[j].getAverageRating() < staff[j+1].getAverageRating()) {
                    StaffMember tmp = staff[j];
                    staff[j] = staff[j+1];
                    staff[j+1] = tmp;
                }
            }
        }

        // 8) Display results
        System.out.println("\n--- Staff by Avg Rating (High→Low) ---");
        for (StaffMember sm : staff) {
            double avg = sm.getAverageRating();
            String level = avg >= 4.5 ? "Excellent" : avg >= 3.0 ? "Good" : "Poor";
            System.out.printf("%s — Avg: %.2f (%s)%n", sm.getName(), avg, level);
        }

        System.out.println("\n--- Staff Eligible for Promotion (5.0) ---");
        for (StaffMember sm : staff) {
            if (sm.getAverageRating() == 5.0) {
                System.out.println(sm.getName());
            }
        }

        System.out.println("\n--- Staff Experience, Pay, Salary & Net Income ---");
        for (StaffMember sm : staff) {
            int exp = sm.calculateExperience(CURRENT_YEAR);
            double weekly = sm.calculateWeeklyPay();
            double annual = sm.calculateAnnualSalary();
            double net    = sm.calculateNetIncome(TAX_RATE);
            System.out.printf("%s: %d yrs, Weekly $%.2f, Annual $%.2f, Net $%.2f%n",
                              sm.getName(), exp, weekly, annual, net);
        }

        System.out.println("\n--- Infant Teachers ---");
        for (StaffMember sm : staff) {
            if ("Infant Teacher".equalsIgnoreCase(sm.getJobTitle())) {
                System.out.println(sm.getName());
            }
        }

        double highestBill = -1, lowestBill = Double.MAX_VALUE;
        int youngestAge = Integer.MAX_VALUE, oldestAge = -1;

        System.out.println("\n--- Children Status & Billing ---");
        for (Child c : kids) {
            int age = c.calculateAge(CURRENT_YEAR);
            double bill0     = c.calculateWeeklyBill(age);
            double discPct   = c.calculateDiscountPercent(bill0);
            double afterDisc = bill0 * (1 - discPct);
            double timePct   = c.calculateTimeFeePercent();
            double total     = afterDisc * (1 + timePct);
            String ageSt     = c.getAgeStatus(age);
            String discLab   = c.getDiscountLabel(discPct);
            String timeMsg   = c.getTimeFeeMessage(timePct);

            System.out.printf("%s (%s, Age %d): $%.2f/week, %s off, %s, Total $%.2f%n",
                              c.getName(), ageSt, age, bill0, discLab, timeMsg, total);

            if (age < youngestAge)      youngestAge = age;
            if (age > oldestAge)        oldestAge   = age;
            if (total > highestBill)    highestBill = total;
            if (total < lowestBill)     lowestBill  = total;
        }
        System.out.printf("Youngest: %d, Oldest: %d%n", youngestAge, oldestAge);
        System.out.printf("Highest Bill: $%.2f, Lowest Bill: $%.2f%n", highestBill, lowestBill);

        System.out.println("\n--- Parent Ratings ---");
        for (int p = 0; p < parentCount; p++) {
            System.out.printf("Parent #%d: ", p+1);
            for (int s = 0; s < staffCount; s++) {
                System.out.printf("%s=%.1f  ", staff[s].getName(), ratings[p][s]);
            }
            System.out.println();
        }

        scanner.close();
    }
}
