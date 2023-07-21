package Jdbc1;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/Flats?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "Vika_2003";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true) {
                    System.out.println("1: add flat");
                    System.out.println("2: add random flats");
                    System.out.println("3: delete flat");
                    System.out.println("4: change flat's price");
                    System.out.println("5: view flats");
                    System.out.println("6: choice of flat");
                    System.out.println("0: exit");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addFlat(sc);
                            break;
                        case "2":
                            insertRandomFlats(sc);
                            break;
                        case "3":
                            deleteFlat(sc);
                            break;
                        case "4":
                            changeFlatPrice(sc);
                            break;
                        case "5":
                            viewFlats();
                            break;
                        case "6":
                            choiseFlats(sc);
                            break;
                        case "0":
                            return;
                        default:
                            System.err.println("Unknown command! Try again...");
                            break;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(30) NOT NULL, address VARCHAR(30) NOT NULL, area INT, numberOfRooms INT, price INT)");
        } finally {
            st.close();
        }
    }


    private static void addFlat(Scanner sc) throws SQLException {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter area: ");
        String sArea = sc.nextLine();
        double area = Double.parseDouble(sArea);
        System.out.print("Enter number of rooms: ");
        String sNumberOfRooms = sc.nextLine();
        int numberOfRooms = Integer.parseInt(sNumberOfRooms);
        System.out.print("Enter price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, address, area, numberOfRooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, numberOfRooms);
            ps.setDouble(5,  price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE


        } finally {
            ps.close();
        }
    }

    private static void deleteFlat(Scanner sc) throws SQLException {
        System.out.print("Enter adress: ");
        String address = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("DELETE FROM Flats WHERE address = ?");
        try {
            ps.setString(1, address);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
    }

    private static void changeFlatPrice(Scanner sc) throws SQLException {
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter new price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);

        PreparedStatement ps = conn.prepareStatement("UPDATE Flats SET price = ? WHERE address = ?");
        try {
            ps.setDouble(1, price);
            ps.setString(2, address);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
    }

    private static void insertRandomFlats(Scanner sc) throws SQLException {
        System.out.print("Enter flats count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);
        Random rnd = new Random();
        final String[] districts = {"Goloseevskyi", "Obolonskyi", "Pecherskyi", "Podolskyi", "Svyatoshinskyi", "Solomenskyi", "Shevchenkivskyi"};
        final String[] adresses = {"Lesi Ukrainki", "Grushevskogo", "Orlika", "Granitna", "Khreshatik", "Shcherbakovskogo"};
        conn.setAutoCommit(false); // enable transactions
        try {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, address, area, numberOfRooms, price) VALUES(?, ?, ?, ?, ?)");
                try {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, districts[rnd.nextInt(districts.length)] );
                        ps.setString(2, adresses[rnd.nextInt(adresses.length)] + ", " + rnd.nextInt(200));
                        ps.setDouble(3, 30 + rnd.nextInt(71) + rnd.nextDouble());
                        ps.setInt(4, 1 + rnd.nextInt(6));
                        ps.setDouble(5, 6000 + rnd.nextInt(194001) + rnd.nextDouble());

                        ps.executeUpdate();
                    }
                    conn.commit();
                } finally {
                    ps.close();
                }
            } catch (Exception ex) {
                conn.rollback();
            }
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }


    private static void viewFlats() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Flats");
        try {

            ResultSet rs = ps.executeQuery();

            try {
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }
    private static void viewFlats(String str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(str);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close(); // rs can't be null according to the docs
            }
        } finally {
            ps.close();
        }
    }
    private static void choiseFlats(Scanner sc) throws SQLException {
        System.out.println("1: selection by district");
        System.out.println("2: selection by price");
        System.out.println("3: selection by area");
        System.out.println("4: selection by number of room");
        System.out.print("-> ");

        String s = sc.nextLine();
        switch (s) {
            case "1":
                selectionByDistrict(sc);
                break;
            case "2":
                selectionByPrice(sc);
                break;
            case "3":
                selectionByArea(sc);
                break;
            case "4":
                selectionByRoom(sc);
                break;
            default:
                return;
        }
    }
    private static void selectionByDistrict(Scanner sc) throws SQLException {
        System.out.println("Enter district (Goloseevskyi, Obolonskyi, Pecherskyi, Podolskyi, Svyatoshinskyi, Solomenskyi or Shevchenkivskyi):");
        String district = sc.nextLine();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Flats WHERE district = '").append(district).append("';");
        viewFlats(sb.toString());
    }

    private static void selectionByPrice(Scanner sc) throws SQLException {
        System.out.println("Enter minimum price:");
        String priceMin = sc.nextLine();

        System.out.println("Enter maximum price:");
        String priceMax = sc.nextLine();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Flats WHERE price BETWEEN ").append(priceMin).append(" and ").append(priceMax).append(";");

        viewFlats(sb.toString());
    }
    private static void selectionByArea(Scanner sc) throws SQLException {
        System.out.println("Enter the minimum area:");
        String areaMin = sc.nextLine();

        System.out.println("Enter the maximum area:");
        String areaMax = sc.nextLine();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Flats WHERE area BETWEEN ").append(areaMin).append(" AND ").append(areaMax).append(";");

        viewFlats(sb.toString());
    }
    private static void selectionByRoom(Scanner sc) throws SQLException {
        System.out.print("Enter the minimum number of rooms: ");
        String numberMin = sc.nextLine();

        System.out.print("Enter the maximum number of rooms: ");
        String numberMax = sc.nextLine();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Flats WHERE numberOfRooms BETWEEN ").append(numberMin).append(" AND ").append(numberMax).append(";");

        viewFlats(sb.toString());
    }
}
