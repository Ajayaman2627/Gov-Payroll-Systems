package ui;

import model.PayrollRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.util.List;

public class PayslipDialog extends JDialog {

    private static final String[] MONTHS = {
        "","January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private final JEditorPane editorPane;

    public PayslipDialog(Frame parent, PayrollRecord record, int month, int year,
                         List<Object[]> allowances, List<Object[]> deductions) {
        super(parent, "Payslip — " + record.getEmpName(), true);

        String html = buildHtml(record, month, year, allowances, deductions);
        editorPane = new JEditorPane("text/html", html);
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(editorPane);
        scroll.setPreferredSize(new Dimension(600, 650));

        JButton btnPrint = new JButton("Print");
        JButton btnClose = new JButton("Close");
        btnPrint.setFont(btnPrint.getFont().deriveFont(Font.BOLD));

        btnPrint.addActionListener(e -> print());
        btnClose.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        footer.add(btnPrint);
        footer.add(btnClose);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void print() {
        try {
            editorPane.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
        }
    }

    private String buildHtml(PayrollRecord rec, int month, int year,
                             List<Object[]> allowances, List<Object[]> deductions) {
        String period = MONTHS[month] + " " + year;
        String grade  = rec.getGradeName() == null || rec.getGradeName().isEmpty() ? "—" : rec.getGradeName();

        StringBuilder sb = new StringBuilder(2048);
        sb.append("<html><body style='font-family:Arial,sans-serif; font-size:13px; margin:24px;'>");

        // ── Header ──────────────────────────────────────────────────
        sb.append("<div style='text-align:center; border-bottom:2px solid #003366; padding-bottom:12px; margin-bottom:16px;'>");
        sb.append("<h2 style='margin:0; color:#003366; font-size:18px;'>GOVERNMENT PAYROLL SYSTEM</h2>");
        sb.append("<h3 style='margin:4px 0; color:#444; font-size:14px; font-weight:normal;'>EMPLOYEE PAYSLIP</h3>");
        sb.append("<p style='margin:2px 0; font-size:12px;'>Pay Period: <b>").append(period).append("</b></p>");
        sb.append("</div>");

        // ── Employee info ────────────────────────────────────────────
        sb.append("<table width='100%' cellpadding='4' cellspacing='0' style='margin-bottom:16px; font-size:12px;'>");
        row2(sb, "Employee Name:", rec.getEmpName(), "Employee ID:", "EMP-" + String.format("%04d", rec.getEmpId()));
        row2(sb, "Department:",    rec.getDeptName(), "Grade:",       grade);
        row2(sb, "Step:",          String.valueOf(rec.getStepNo()), "Run ID:", "#" + rec.getRunId());
        sb.append("</table>");

        // ── Earnings ────────────────────────────────────────────────
        sb.append("<table width='100%' cellpadding='5' cellspacing='0' style='border-collapse:collapse; margin-bottom:16px;'>");
        sb.append("<tr style='background:#003366; color:white;'>");
        sb.append("<th style='text-align:left; padding:7px;'>Earnings</th>");
        sb.append("<th style='text-align:right; padding:7px;'>Amount (GHS)</th></tr>");

        // Basic salary row
        sb.append("<tr style='background:#f0f4ff;'>");
        sb.append("<td style='padding:6px;'>Basic Salary</td>");
        sb.append("<td style='padding:6px; text-align:right;'>").append(fmt(rec.getBasicSalary())).append("</td></tr>");

        // Allowance rows
        boolean alt = true;
        for (Object[] a : allowances) {
            String name      = (String) a[0];
            String mode      = (String) a[1];
            BigDecimal rawVal = (BigDecimal) a[2];
            BigDecimal computed = (BigDecimal) a[3];
            String label = name + (mode.equals("PERCENT")
                ? " (" + rawVal.stripTrailingZeros().toPlainString() + "%)" : "");
            sb.append(alt ? "<tr>" : "<tr style='background:#f8faff;'>");
            sb.append("<td style='padding:5px 6px 5px 20px;'>").append(label).append("</td>");
            sb.append("<td style='padding:5px 6px; text-align:right;'>").append(fmt(computed)).append("</td></tr>");
            alt = !alt;
        }

        // Gross pay
        sb.append("<tr style='background:#dce8ff; font-weight:bold; border-top:1px solid #003366;'>");
        sb.append("<td style='padding:7px;'>GROSS PAY</td>");
        sb.append("<td style='padding:7px; text-align:right;'>").append(fmt(rec.getGrossPay())).append("</td></tr>");
        sb.append("</table>");

        // ── Deductions ───────────────────────────────────────────────
        sb.append("<table width='100%' cellpadding='5' cellspacing='0' style='border-collapse:collapse; margin-bottom:16px;'>");
        sb.append("<tr style='background:#7b0000; color:white;'>");
        sb.append("<th style='text-align:left; padding:7px;'>Deductions</th>");
        sb.append("<th style='text-align:right; padding:7px;'>Amount (GHS)</th></tr>");

        if (deductions.isEmpty()) {
            sb.append("<tr><td colspan='2' style='padding:6px; color:#888; font-style:italic;'>No deductions applied</td></tr>");
        } else {
            alt = true;
            for (Object[] d : deductions) {
                String name    = (String) d[0];
                String mode    = (String) d[1];
                double rawVal  = (Double)  d[2];
                double computed = (Double) d[3];
                String label = name + (mode.equals("PERCENT") ? " (" + rawVal + "%)" : "");
                sb.append(alt ? "<tr>" : "<tr style='background:#fff5f5;'>");
                sb.append("<td style='padding:5px 6px 5px 20px;'>").append(label).append("</td>");
                sb.append("<td style='padding:5px 6px; text-align:right;'>").append(String.format("%,.2f", computed)).append("</td></tr>");
                alt = !alt;
            }
        }

        // Total deductions
        sb.append("<tr style='background:#ffe0e0; font-weight:bold; border-top:1px solid #7b0000;'>");
        sb.append("<td style='padding:7px;'>TOTAL DEDUCTIONS</td>");
        sb.append("<td style='padding:7px; text-align:right;'>").append(fmt(rec.getTotalDeductions())).append("</td></tr>");
        sb.append("</table>");

        // ── Net Pay ──────────────────────────────────────────────────
        sb.append("<div style='background:#003366; color:white; padding:14px 16px; text-align:right; ");
        sb.append("font-size:16px; font-weight:bold; border-radius:4px; margin-bottom:24px;'>");
        sb.append("NET PAY: &nbsp; GHS ").append(fmt(rec.getNetPay()));
        sb.append("</div>");

        // ── Footer ───────────────────────────────────────────────────
        sb.append("<div style='font-size:10px; color:#999; border-top:1px solid #ddd; padding-top:8px; text-align:center;'>");
        sb.append("This payslip is computer-generated. No signature required. &nbsp;|&nbsp; Government Payroll System");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private static void row2(StringBuilder sb,
                             String l1, String v1, String l2, String v2) {
        sb.append("<tr>");
        sb.append("<td style='width:20%; font-weight:bold; color:#555; padding:3px 6px;'>").append(l1).append("</td>");
        sb.append("<td style='width:30%; padding:3px 6px;'>").append(v1).append("</td>");
        sb.append("<td style='width:20%; font-weight:bold; color:#555; padding:3px 6px;'>").append(l2).append("</td>");
        sb.append("<td style='width:30%; padding:3px 6px;'>").append(v2).append("</td>");
        sb.append("</tr>");
    }

    private static String fmt(BigDecimal val) {
        return val == null ? "0.00" : String.format("%,.2f", val);
    }
}
