package com.studentgrade;

import com.studentgrade.service.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GradeServiceTest {
    private GradeService gradeService;

    @BeforeEach
    void setUp() { gradeService = new GradeService(); }

    @Test void testGradeA_Plus()  { assertEquals("A+", gradeService.calculateGrade(95)); }
    @Test void testGradeA()       { assertEquals("A",  gradeService.calculateGrade(85)); }
    @Test void testGradeB_Plus()  { assertEquals("B+", gradeService.calculateGrade(75)); }
    @Test void testGradeB()       { assertEquals("B",  gradeService.calculateGrade(65)); }
    @Test void testGradeC()       { assertEquals("C",  gradeService.calculateGrade(55)); }
    @Test void testGradeD()       { assertEquals("D",  gradeService.calculateGrade(45)); }
    @Test void testGradeF()       { assertEquals("F",  gradeService.calculateGrade(30)); }
    @Test void testGradeBoundary(){ assertEquals("A+", gradeService.calculateGrade(90)); }

    @Test
    void testCalculateAverage() {
        List<Double> marks = Arrays.asList(80.0, 70.0, 90.0, 60.0);
        assertEquals(75.0, gradeService.calculateAverage(marks));
    }

    @Test
    void testCalculateHighest() {
        List<Double> marks = Arrays.asList(80.0, 95.0, 60.0);
        assertEquals(95.0, gradeService.calculateHighest(marks));
    }

    @Test
    void testCalculateLowest() {
        List<Double> marks = Arrays.asList(80.0, 95.0, 42.0);
        assertEquals(42.0, gradeService.calculateLowest(marks));
    }

    @Test
    void testEmptyMarksAverage() {
        assertEquals(0.0, gradeService.calculateAverage(List.of()));
    }

    @Test
    void testIsPassOverall_Pass() {
        List<Double> marks = Arrays.asList(50.0, 60.0, 70.0);
        assertTrue(gradeService.isPassOverall(marks));
    }

    @Test
    void testIsPassOverall_Fail() {
        List<Double> marks = Arrays.asList(50.0, 39.0, 70.0); // 39 is below passing
        assertFalse(gradeService.isPassOverall(marks));
    }
}
