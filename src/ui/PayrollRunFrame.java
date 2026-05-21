package ui;

import dao.PayrollDAO;
import model.PayrollRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PayrollRunFrame extends JFrame {

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private final PayrollDAO dao = new PayrollDAO();

    private final JComboBox<String> cmbMonth = new JComboBox<>(MONTH_NAMES);
    private final JSpinner spnYear  = new JSpinner(new SpinnerNumberModel(
            java.time.LocalDate.now().getYear(), 2000, 2100, 1));
    private final JComboBox<RunItem> cmbHistory = new JComboBox<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Emp ID","Name","Department","Grade","Step",
                         "Basic (GHS)","Allowances","Gross Pay","Deductions","Net Pay"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JButton btnViewPayslip = new JButton("View Payslip for Selected Employee");

    private List<PayrollRecord> currentRecords;

    public PayrollRunFrame() {
        setTitle("Payroll Run");
        setSize(1080, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- Run new payroll panel ----
        JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        runPanel.setBorder(BorderFactory.createTitledBorder("Run New Payroll"));
        cmbMonth.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
        runPanel.add(new JLabel("Month:"));
        runPanel.add(cmbMonth);
        runPanel.add(new JLabel("Year:"));
        runPanel.add(spnYear);
        JButton btnRun = new JButton("Run Payroll");
        btnRun.setFont(btnRun.getFont().deriveFont(Font.BOLD));
        runPanel.add(btnRun);

        // ---- Load history panel ----
        JPanel histPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        histPanel.setBorder(BorderFactory.createTitledBorder("View Previous Run"));
        cmbHistory.setPreferredSize(new Dimension(220, 26));
        histPanel.add(new JLabel("Select run:"));
        histPanel.add(cmbHistory);
        JButton btnLoad = new JButton("Load");
        histPanel.add(btnLoad);

        JPanel topWrapper = new JPanel(new GridLayout(1, 2, 8, 0));
        topWrapper.add(runPanel);
        topWrapper.add(histPanel);

        // ---- Table ----
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scroll = new JScrollPane(table);

        // ---- Bottom bar ----
        btnViewPayslip.setEnabled(false);
        btnViewPayslip.setFont(btnViewPayslip.getFont().deriveFont(Font.BOLD));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottom.add(btnViewPayslip);

        setLayout(new BorderLayout(0, 0));
        add(topWrapper, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // ---- Listeners ----
        btnRun.addActionListener(e -> runPayroll());
        btnLoad.addActionListener(e -> loadSelectedRun());
        btnViewPayslip.addActionListener(e -> openPayslip());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnViewPayslip.setEnabled(table.getSelectedRow() >= 0 && currentRecords != null);
        });

        loadHistory();
    }

    private void loadHistory() {
        cmbHistory.removeAllItems();
        try {
            for (Object[] r : dao.getAllRuns())
                cmbHistory.addItem(new RunItem((int) r[0], (int) r[1], (int) r[2]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void runPayroll() {
        int month = cmbMonth.getSelectedIndex() + 1;
        int year  = (Integer) spnYear.getValue();
        try {
            if (dao.runAlreadyExists(month, year)) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "A payroll run already exists for " + MONTH_NAMES[month - 1] + " " + year + ".\n" +
                        "Do you want to create another run for the same period?",
                        "Duplicate Run", JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) return;
            }

            int runId = dao.runPayroll(month, year);
            if (runId < 0) {
                JOptionPane.showMessageDialog(this, "Payroll run failed — check database tables.");
                return;
            }

            currentRecords = dao.getRecordsByRun(runId);
            populateTable(currentRecords);
            loadHistory();

            JOptionPane.showMessageDialog(this,
                    "Payroll run completed for " + MONTH_NAMES[month - 1] + " " + year + ".\n" +
                    currentRecords.size() + " employee(s) processed.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage() +
                    "\n\nMake sure payroll_run and payroll_record tables exist.");
        }
    }

    private void loadSelectedRun() {
        RunItem item = (RunItem) cmbHistory.getSelectedItem();
        if (item == null) { JOptionPane.showMessageDialog(this, "No run selected."); return; }
        try {
            currentRecords = dao.getRecordsByRun(item.runId);
            populateTable(currentRecords);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading run: " + ex.getMessage());
        }
    }

    private void populateTable(List<PayrollRecord> records) {
        tableModel.setRowCount(0);
        for (PayrollRecord r : records) {
            tableModel.addRow(new Object[]{
                r.getEmpId(),
                r.getEmpName(),
                r.getDeptName(),
                r.getGradeName().isEmpty() ? "—" : r.getGradeName(),
                r.getStepNo(),
                String.format("%,.2f", r.getBasicSalary()),
                String.format("%,.2f", r.getTotalAllowances()),
                String.format("%,.2f", r.getGrossPay()),
                String.format("%,.2f", r.getTotalDeductions()),
                String.format("%,.2f", r.getNetPay())
            });
        }
        btnViewPayslip.setEnabled(false);
    }

    private void openPayslip() {
        int row = table.getSelectedRow();
        if (row < 0 || currentRecords == null) return;
        PayrollRecord record = currentRecords.get(row);
        int month = -1;
        RunItem selected = (RunItem) cmbHistory.getSelectedItem();
        if (selected != null) month = selected.month;
        // fall back to current combo selection
        if (month < 0) month = cmbMonth.getSelectedIndex() + 1;
        int year = (Integer) spnYear.getValue();
        try {
            List<Object[]> allowances = dao.getAllowanceDetail(record.getEmpId());
            List<Object[]> deductions = dao.getDeductionDetail(record.getEmpId());
            new PayslipDialog(this, record, month, year, allowances, deductions).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payslip data: " + ex.getMessage());
        }
    }

    private static class RunItem {
        final int runId, month, year;
        RunItem(int runId, int month, int year) { this.runId = runId; this.month = month; this.year = year; }
        @Override public String toString() {
            return "Run #" + runId + "  —  " + MONTH_NAMES[month - 1] + " " + year;
        }
    }
}
