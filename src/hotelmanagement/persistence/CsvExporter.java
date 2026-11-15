package hotelmanagement.persistence;

import hotelmanagement.exceptions.CsvExportException;
import hotelmanagement.exceptions.PersistenceException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

//dumps a whole table to <folder>/<table>.csv this makes the backup the assigment asks for
final class CsvExporter {

    private CsvExporter() { }

    static void export(String table, String folder) throws PersistenceException {
        Path dir = Paths.get(folder);
        Path file = dir.resolve(table + ".csv");

        try {
            Files.createDirectories(dir);

            try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM " + table);
                 PrintWriter out = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {

                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();

                StringBuilder header = new StringBuilder();
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) header.append(',');
                    header.append(md.getColumnLabel(i));
                }
                out.println(header);

                while (rs.next()) {
                    StringBuilder line = new StringBuilder();
                    for (int i = 1; i <= cols; i++) {
                        if (i > 1) line.append(',');
                        String value = rs.getString(i);
                        line.append(value == null ? "" : "\"" + value.replace("\"", "\"\"") + "\"");
                    }
                    out.println(line);
                }
            }
        } catch (SQLException | IOException e) {
            throw new CsvExportException("Could not export table " + table + " to " + file);
        }
    }
}