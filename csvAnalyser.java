import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

public class csvAnalyser {

    private static JTextArea textArea;
    private static JFrame frame;
    private static JTable table;
    private static DefaultTableModel tableModel;
    private static JTable resultTable;
    private static DefaultTableModel resultTableModel;
    private static JLabel fileInfoLabel;
    private static File selectedFile;
    private static String statistics;

    public static void main(String[] args) {
        frame = new JFrame("CSV Analyser");
        textArea = new JTextArea();

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"column1", "column2", "column3", "column4"});
        table = new JTable(tableModel);

        resultTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"column1", "column2", "column3", "column4"});
        resultTable = new JTable(resultTableModel);

        fileInfoLabel = new JLabel();

        JScrollPane tableScrollPane = new JScrollPane(table);
        JScrollPane resultTableScrollPane = new JScrollPane(resultTable);

        JPanel buttonPanel = new JPanel();

        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenu help = new JMenu("Help");
        JMenu importCSV = new JMenu("Import CSV");
        JMenu calculateStatistics = new JMenu("Calculate Network Statistics");

        JButton analyse = new JButton("Analyse");
        JButton reset = new JButton("Reset");

        JMenuItem open = new JMenuItem("Open File");
        JMenuItem saveAs = new JMenuItem("Save As");
        JMenuItem close = new JMenuItem("Close");

        JMenuItem helpFile = new JMenuItem("Help");

        JMenuItem openCSVFile = new JMenuItem("Open File Explorer");

        JMenuItem showStatistics = new JMenuItem("Show Network Statistics");
        JMenuItem saveStatistics = new JMenuItem("Save as");

        JMenuItem pickIPs = new JMenuItem("Pick IP");

        menuBar.add(file);
        menuBar.add(help);
        menuBar.add(importCSV);
        menuBar.add(calculateStatistics);

        buttonPanel.add(analyse);
        buttonPanel.add(reset);
        file.add(open);
        file.add(saveAs);
        file.add(close);

        help.add(helpFile);

        importCSV.add(openCSVFile);

        calculateStatistics.add(showStatistics);

        calculateStatistics.add(pickIPs);

        frame.setLayout(new BorderLayout());
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setJMenuBar(menuBar);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        close.addActionListener(e -> System.exit(0));

        reset.addActionListener(e -> {
            textArea.setText("");
            tableModel.setRowCount(0);
        });

        pickIPs.addActionListener(e -> {
            // Add your implementation for pickIPs ActionListener
        });

        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                displayFileInfo(selectedFile);

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String headerLine = reader.readLine();
                    String[] columnNames = headerLine.split(",");

                    tableModel.setColumnIdentifiers(columnNames);
                    tableModel.setRowCount(0);

                    resultTableModel.setColumnIdentifiers(columnNames);
                    resultTableModel.setRowCount(0);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] rowData = line.split(",");
                        tableModel.addRow(rowData);
                    }
                    JOptionPane.showMessageDialog(null, "File loaded successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading file");
                }
            }
        });

        helpFile.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(new File("C:\\Users\\samke\\Documents\\helping.txt"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        saveAs.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                }
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    String textToSave = textArea.getText();

                    writer.write(textToSave);
                    JOptionPane.showMessageDialog(null, "File saved successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error saving file");
                }
            }
        });

        openCSVFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                displayFileInfo(selectedFile);

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String headerLine = reader.readLine();
                    String[] columnNames = headerLine.split(",");

                    tableModel.setRowCount(0);
                    tableModel.setColumnIdentifiers(columnNames);

                    resultTableModel.setRowCount(0);
                    resultTableModel.setColumnIdentifiers(columnNames);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] rowData = line.split(",");
                        tableModel.addRow(rowData);
                    }

                    JOptionPane.showMessageDialog(null, "File loaded successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading file");
                }
            }
        });

        analyse.addActionListener(e -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder fileContent = new StringBuilder();
                List<String> linesOver200 = new ArrayList<>();

                String headerLine = reader.readLine();
                String[] columnNames = headerLine.split(",");

                resultTableModel.setRowCount(0);
                resultTableModel.setColumnIdentifiers(columnNames);

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    resultTableModel.addRow(rowData);

                    String[] columns = line.split(",");

                    if (columns.length >= 4) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = dateFormat.parse(columns[0]);

                            int value = Integer.parseInt(columns[3]);
                            if (value > 200) {
                                linesOver200.add(line);
                            }
                        } catch (ParseException | NumberFormatException ex) {
                            System.err.println("Error processing line: " + line);
                        }
                    }
                }

                textArea.setText(fileContent.toString());
                
                updateTable(linesOver200);
                saveLinesOver200(linesOver200);
                displayLinesOver200(linesOver200);

                JOptionPane.showMessageDialog(frame, "CSV file analyzed successfully!");
            } catch (IOException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error analyzing CSV file: " + ex.getMessage());
            }
        });

        showStatistics.addActionListener(e -> showNetworkStatistics(selectedFile));

        saveStatistics.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(statistics);
                    JOptionPane.showMessageDialog(null, "Statistics saved");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error saving statistics");
                }
            }
        });
    }

    private static void showNetworkStatistics(File file) {
        int numberOfRows = tableModel.getRowCount();
        double averageFlows = calculateAverageFlows();
        Date[] dateRange = calculateDateRange();
        int numberOfDifferentIPs = calculateDifferentIPs();

        String statisticsMessage = "Number of Rows: " + numberOfRows + "\n" +
                "Average Number of Flows: " + averageFlows + "\n" +
                "Date Range: " + formatDates(dateRange[0], dateRange[1]) + "\n" +
                "Number of Different IPs: " + numberOfDifferentIPs;

        JFrame statisticsFrame = new JFrame("Network Statistics");
        JTextArea statisticsTextArea = new JTextArea(statisticsMessage);

        statisticsFrame.add(new JScrollPane(statisticsTextArea));
        statisticsFrame.setSize(600, 400);
        statisticsFrame.setLocationRelativeTo(null);
        statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statisticsFrame.setVisible(true);

        JButton saveAsButton = new JButton("Save As");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveAsButton);
        statisticsFrame.add(BorderLayout.SOUTH, buttonPanel);
        saveAsButton.addActionListener(e -> saveStatisticsAs(statisticsMessage));
    }

    private static String formatDates(Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(startDate) + " to " + dateFormat.format(endDate);
    }

    private static void displayFileInfo(File file) {
        if (frame == null) {
            System.err.println("Error: frame is null");
            return;
        }
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);

        JPanel fileInfoPanel = new JPanel(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        fileInfoPanel.add(iconLabel, BorderLayout.WEST);

        JTextArea fileInfoTextArea = new JTextArea();
        fileInfoTextArea.append("File Name: " + file.getName() + "\n");

        fileInfoPanel.add(new JScrollPane(fileInfoTextArea), BorderLayout.CENTER);

        frame.getContentPane().add(BorderLayout.NORTH, fileInfoPanel);

        frame.repaint();

        fileInfoLabel.setIcon(icon);
        fileInfoLabel.setText("File Name: " + file.getName());

        textArea.setText("");
        textArea.append("File Name: " + file.getName());
        textArea.setCaretPosition(textArea.getDocument().getLength());

        if (icon != null && icon instanceof ImageIcon) {
            ImageIcon imageIcon = (ImageIcon) icon;
            JLabel iconLabel2 = new JLabel(imageIcon);
            frame.getContentPane().add(BorderLayout.WEST, iconLabel);
        }
    }

    private static void analyseCSVFile(File fileToOpen) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
            StringBuilder fileContent = new StringBuilder();
            List<String> linesOver200 = new ArrayList<>();

            String headerLine = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");

                String[] columns = line.split(",");

                if (columns.length >= 4) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = dateFormat.parse(columns[0]);

                        int value = Integer.parseInt(columns[3]);
                        if (value > 200) {
                            linesOver200.add(line);
                        }
                    } catch (ParseException | NumberFormatException ex) {
                        System.err.println("Error processing line: " + line);
                    }
                }
            }

            textArea.setText(fileContent.toString());
            updateTable(linesOver200);
            saveLinesOver200(linesOver200);
            displayLinesOver200(linesOver200);

            JOptionPane.showMessageDialog(frame, "CSV file analyzed successfully!");
        } catch (IOException | NumberFormatException ex) {

            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error analyzing CSV file: " + ex.getMessage());
        }
    }

    private static void updateTable(List<String> linesOver200) {
        resultTableModel.setRowCount(0);

        for (String line : linesOver200) {
            String[] rowData = line.split(",");
            resultTableModel.addRow(rowData);
        }
    }

    private static void saveLinesOver200(List<String> linesOver200) {
        try (FileWriter writer = new FileWriter(new File("analysis.txt"))) {
            for (String line : linesOver200) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayLinesOver200(List<String> linesOver200) {
        JFrame resultFrame = new JFrame("Lines With Anomalous Flow Values");

        DefaultTableModel resultTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"column1", "column2", "column3", "column4"});
        JTable resultTable = new JTable(resultTableModel);

        for (String line : linesOver200) {
        String[] rowData = line.split(",");
            resultTableModel.addRow(rowData);
        }

        JScrollPane resultScrollPane = new JScrollPane(resultTable);

        JButton exportResultsButton = new JButton("Export Results");
        JButton produceReportButton = new JButton("Produce Report");

        exportResultsButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }

                try (FileWriter writer = new FileWriter(fileToSave)) {
                    for (String line : linesOver200) {
                        writer.write(line + "\n");
                    }

                    JOptionPane.showMessageDialog(null, "Results exported successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error exporting results");
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exportResultsButton);
        buttonPanel.add(produceReportButton);

        resultFrame.setLayout(new BorderLayout());

        resultFrame.add(BorderLayout.CENTER, resultScrollPane);
        resultFrame.add(BorderLayout.SOUTH, buttonPanel);

        resultFrame.setSize(600, 400);
        resultFrame.setVisible(true);
    }
    private static double calculateAverageFlows() {
        int columnIndex = 3;
        double totalFlows = 0;
        int validCount = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, columnIndex);

            if (value != null) {
                try {
                    if (value instanceof Number) {
                        totalFlows += ((Number) value).doubleValue();
                        validCount++;
                    } else {
                        double numericValue = Double.parseDouble(value.toString());
                        totalFlows += numericValue;
                        validCount++;
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Value at row " + i + ", column " + columnIndex + " is not a valid number: " + value);
                }
            }
        }

        return validCount > 0 ? totalFlows / validCount : 0.0;
    }

    private static Date[] calculateDateRange() {
        int columnIndex = 0;

        Date startDate = null;
        Date endDate = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                Object value = tableModel.getValueAt(i, columnIndex);
                if (value instanceof String) {
                    Date date = dateFormat.parse((String) value);

                    if (startDate == null || date.before(startDate)) {
                        startDate = date;
                    }
                    if (endDate == null || date.after(endDate)) {
                        endDate = date;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (startDate == null || endDate == null) {
            return new Date[]{null, null};
        }

        return new Date[]{startDate, endDate};
    }

    private static int calculateDifferentIPs() {
        int columnIndex = 1;
        List<String> uniqueIPs = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, columnIndex);

            if (value instanceof String) {
                String ip = (String) value;
                if (!uniqueIPs.contains(ip)) {
                    uniqueIPs.add(ip);
                }
            }
        }

        return uniqueIPs.size();
    }

    private static void saveStatisticsAs(String statistics) {
        JFileChooser fileChooser = new JFileChooser();
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(statistics);
                JOptionPane.showMessageDialog(null, "Statistics saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving statistics");
            }
        }
    }
}
