import java.awt.*;
import java.io.*;
import javax.swing.*;

public class StudentDatabaseApp extends JFrame {
    private static final String FILE_NAME = "students.csv";
    
    private JTextField idField, nameField;
    private JTextArea outputArea;

    public StudentDatabaseApp() {
        setTitle("Student Database Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> insertStudent());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> deleteStudent());
        inputPanel.add(deleteButton);

        JButton listButton = new JButton("List Students");
        listButton.addActionListener(e -> listStudents());
        inputPanel.add(listButton);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void insertStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                outputArea.append("✗ Student name cannot be empty.\n");
                return;
            }

            // Check if student already exists
            java.util.List<String> lines = new java.util.ArrayList<>();

            boolean exists = false;
            File file = new File(FILE_NAME);

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                        if (line.startsWith(id + ",")) {
                            exists = true;
                            break;
                        }
                    }
                }
            }

            if (exists) {
                outputArea.append("✗ A student with ID " + id + " already exists.\n");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(id + "," + name);
                writer.newLine();
                outputArea.append("✓ Student " + name + " (ID: " + id + ") added successfully\n");
            }

            clearInputFields();

        } catch (NumberFormatException e) {
            outputArea.append("✗ Invalid ID format. Please enter a number.\n");
        } catch (IOException e) {
            outputArea.append("✗ File error: " + e.getMessage() + "\n");
        }
    }

    private void deleteStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp_students.csv");

            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(id + ",")) {
                        found = true;
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            }

            if (found) {
                outputArea.append("✓ Student with ID " + id + " deleted successfully\n");
            } else {
                outputArea.append("✗ No student found with ID " + id + "\n");
            }

            clearInputFields();

        } catch (NumberFormatException e) {
            outputArea.append("✗ Invalid ID format. Please enter a number.\n");
        } catch (IOException e) {
            outputArea.append("✗ File error: " + e.getMessage() + "\n");
        }
    }

    private void listStudents() {
        outputArea.append("---- Student List ----\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean empty = true;
            while ((line = reader.readLine()) != null) {
                outputArea.append(line + "\n");
                empty = false;
            }
            if (empty) {
                outputArea.append("(No students found)\n");
            }
        } catch (FileNotFoundException e) {
            outputArea.append("(No students found - file does not exist yet)\n");
        } catch (IOException e) {
            outputArea.append("✗ Could not read file: " + e.getMessage() + "\n");
        }
    }

    private void clearInputFields() {
        idField.setText("");
        nameField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentDatabaseApp();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
