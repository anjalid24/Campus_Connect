package com.ljuniversity.companion.model;

/**
 * Data class for student info. Extracted from TaskManagerUI.java.
 */
public class StudentInfo {
    public String enrollment, name, div;
    public StudentInfo(String enrollment, String name, String div) {
        this.enrollment = enrollment;
        this.name = name;
        this.div = div;
    }
}