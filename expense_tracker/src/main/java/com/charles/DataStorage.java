package com.charles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private static final String CSV_FILE_PATH = "transactions.csv";
    private final TransactionManager transactionManager;

    public DataStorage(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // Save transactions to CSV file
    public void saveData() throws IOException, SQLException {
        transactionManager.fetchTransactions();
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            writer.write("TransactionId,Type,Amount,Category,Source,Description,Date\n"); // CSV Header
            for (Transaction t : transactionManager.getTransactions()) {
                writer.write(String.format("%s,%s,%.2f,%s,%s,%s,%s\n",
                        t.getTransactionId(), t.getType(), t.getAmount(), t.getCategory(), t.getSource(),
                        t.getDescription(), t.getDate().toString()));
            }
        }
        System.out.println("Data successfully saved to CSV: " + CSV_FILE_PATH + "\n");
    }

    // Load transactions from CSV file
    public List<Transaction> loadData(String filePath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 7) { // Ensure valid format
                    Transaction t = new Transaction();
                    t.setType(data[1]);
                    t.setAmount(Double.parseDouble(data[2]));
                    t.setCategory(data[3]);
                    t.setSource(data[4]);
                    t.setDescription(data[5]);
                    t.setDate(Date.valueOf(data[6]));
                    transactions.add(t);
                }
            }
        }

        System.out.println("Data successfully loaded from: " + filePath);
        return transactions;
    }
}