package com.charles;

import java.sql.SQLException;

public class YearlyReport implements ReportStrategy {

    @Override
    public boolean generateReport(String targetMonth, String targetYear, ReportSummary reportSummary) {
        try {
            return reportSummary.generateReportSummary(targetMonth, targetYear);
        } catch (SQLException ex) {
            return false;
        }
    }
}
