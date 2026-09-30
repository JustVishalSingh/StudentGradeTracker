package com.studentgrade;

import com.studentgrade.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test void testValidEmail()         { assertTrue(ValidationUtil.isValidEmail("student@college.edu")); }
    @Test void testInvalidEmail()       { assertFalse(ValidationUtil.isValidEmail("not-an-email")); }
    @Test void testNullEmail()          { assertFalse(ValidationUtil.isValidEmail(null)); }
    @Test void testValidPhone()         { assertTrue(ValidationUtil.isValidPhone("9876543210")); }
    @Test void testInvalidPhoneShort()  { assertFalse(ValidationUtil.isValidPhone("12345")); }
    @Test void testEmptyPhoneIsOK()     { assertTrue(ValidationUtil.isValidPhone("")); } // phone is optional
    @Test void testNotEmpty()           { assertTrue(ValidationUtil.isNotEmpty("Hello")); }
    @Test void testEmptyString()        { assertFalse(ValidationUtil.isNotEmpty("")); }
    @Test void testNullString()         { assertFalse(ValidationUtil.isNotEmpty(null)); }
    @Test void testValidMarks()         { assertTrue(ValidationUtil.isValidMarks(75)); }
    @Test void testMarksNegative()      { assertFalse(ValidationUtil.isValidMarks(-1)); }
    @Test void testMarksOver100()       { assertFalse(ValidationUtil.isValidMarks(101)); }
    @Test void testValidSemester()      { assertTrue(ValidationUtil.isValidSemester(4)); }
    @Test void testInvalidSemester0()   { assertFalse(ValidationUtil.isValidSemester(0)); }
    @Test void testInvalidSemester9()   { assertFalse(ValidationUtil.isValidSemester(9)); }
}
