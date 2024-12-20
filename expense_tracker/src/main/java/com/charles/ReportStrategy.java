package com.charles;

public interface ReportStrategy {
    public boolean generateReport(String targetMonth, String targetYear, ReportSummary reportSummary);
}