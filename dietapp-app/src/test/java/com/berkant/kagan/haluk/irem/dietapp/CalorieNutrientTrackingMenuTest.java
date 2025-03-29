package com.berkant.kagan.haluk.irem.dietapp;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Unit tests for the CalorieNutrientTrackingMenu class.
 * @author irem
 */
public class CalorieNutrientTrackingMenuTest {

    // Mock services
    private MockCalorieNutrientTrackingService calorieNutrientService;
    private MockMealPlanningService mealPlanningService;
    private MockAuthenticationService authService;
    
    // Menu under test
    private CalorieNutrientTrackingMenu menu;
    
    // Test output stream
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @Before
    public void setUp() {
        // Save original System.out
        originalOut = System.out;
        
        // Setup output capture
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        // Initialize mock services
        calorieNutrientService = new MockCalorieNutrientTrackingService();
        mealPlanningService = new MockMealPlanningService();
        authService = new MockAuthenticationService();
    }
    
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }
    
    @Test
    public void testDisplayMenuAndExitOption() {
        // Simulate user selecting exit option (0)
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify menu was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("===== Calorie and Nutrient Tracking ====="));
        assertTrue(output.contains("1. Set Nutrition Goals"));
        assertTrue(output.contains("0. Return to Main Menu"));
        
        // Verify exit option was selected
        assertEquals(0, calorieNutrientService.functionsCalledCount());
    }
    
    @Test
    public void testInvalidMenuOption() {
        // Simulate user entering invalid option then exit
        String input = "9\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error message was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid choice. Please try again."));
    }
    
    @Test
    public void testNonNumericMenuOption() {
        // Simulate user entering non-numeric option then exit
        String input = "abc\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error message was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid choice. Please try again."));
    }
    
    @Test
    public void testSetNutritionGoals() {
        // Simulate user selecting set nutrition goals (1) then setting goals then exit
        String input = "1\n2000\n50\n250\n70\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify set nutrition goals was called
        assertEquals(1, calorieNutrientService.getSetNutritionGoalsCallCount());
        
        // Verify the parameters
        assertEquals("testuser", calorieNutrientService.getLastUsername());
        assertEquals(2000, calorieNutrientService.getLastCalorieGoal());
        assertEquals(50, calorieNutrientService.getLastProteinGoal(), 0.001);
        assertEquals(250, calorieNutrientService.getLastCarbGoal(), 0.001);
        assertEquals(70, calorieNutrientService.getLastFatGoal(), 0.001);
    }
    
    @Test
    public void testSetNutritionGoalsWithInvalidInputs() {
        // Simulate user entering invalid inputs for each goal
        String input = "1\n-2000\n2000\n-50\n50\n-250\n250\n-70\n70\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error messages were displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Calorie goal must be positive."));
        assertTrue(output.contains("Protein goal must be positive."));
        assertTrue(output.contains("Carbohydrate goal must be positive."));
        assertTrue(output.contains("Fat goal must be positive."));
        
        // Verify set nutrition goals was called with correct values
        assertEquals(1, calorieNutrientService.getSetNutritionGoalsCallCount());
        assertEquals(2000, calorieNutrientService.getLastCalorieGoal());
        assertEquals(50, calorieNutrientService.getLastProteinGoal(), 0.001);
        assertEquals(250, calorieNutrientService.getLastCarbGoal(), 0.001);
        assertEquals(70, calorieNutrientService.getLastFatGoal(), 0.001);
    }
    
    @Test
    public void testSetNutritionGoalsWithNonNumericInputs() {
        // Simulate user entering non-numeric inputs for each goal
        String input = "1\nabc\n2000\nxyz\n50\nwrong\n250\ninvalid\n70\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error messages were displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        
        // Verify set nutrition goals was called with correct values
        assertEquals(1, calorieNutrientService.getSetNutritionGoalsCallCount());
        assertEquals(2000, calorieNutrientService.getLastCalorieGoal());
        assertEquals(50, calorieNutrientService.getLastProteinGoal(), 0.001);
        assertEquals(250, calorieNutrientService.getLastCarbGoal(), 0.001);
        assertEquals(70, calorieNutrientService.getLastFatGoal(), 0.001);
    }
    
    @Test
    public void testViewDailyReport() {
        // Setup test nutrition report
        setupMockNutritionReport();
        
        // Simulate user selecting view daily report (2) then entering date then exit
        String input = "2\n2023\n4\n15\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify get nutrition report was called
        assertEquals(1, calorieNutrientService.getGetNutritionReportCallCount());
        assertEquals("testuser", calorieNutrientService.getLastUsername());
        assertEquals("2023-04-15", calorieNutrientService.getLastDate());
        
        // Verify report was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Nutrition Report for 2023-04-15"));
        assertTrue(output.contains("Calories: 1500 / 2000 (75.0%)"));
        assertTrue(output.contains("Protein: 40.0g / 50.0g (80.0%)"));
    }
    
    @Test
    public void testViewDailyReportWithInvalidDateInputs() {
        // Setup test nutrition report
        setupMockNutritionReport();
        
        // Simulate user entering invalid date values
        String input = "2\n2000\n2023\n13\n4\n32\n15\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error messages were displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Please enter a valid year between 2023 and 2100."));
        assertTrue(output.contains("Please enter a valid month between 1 and 12."));
        assertTrue(output.contains("Please enter a valid day between 1 and 30."));
        
        // Verify report was displayed with correct date
        assertTrue(output.contains("Nutrition Report for 2023-04-15"));
    }
    
    @Test
    public void testViewWeeklyReport() {
        // Setup test nutrition reports
        setupMockWeeklyReport();
        
        // Simulate user selecting view weekly report (3) then entering start date then exit
        String input = "3\n2023\n4\n15\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify get weekly report was called
        assertEquals(1, calorieNutrientService.getGetWeeklyReportCallCount());
        assertEquals("testuser", calorieNutrientService.getLastUsername());
        
        // Verify report was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Weekly Nutrition Report"));
        assertTrue(output.contains("Date: 2023-04-15"));
        assertTrue(output.contains("Date: 2023-04-16"));
        assertTrue(output.contains("Weekly Averages:"));
    }
    
    @Test
    public void testCalculateSuggestedCalories() {
        // Setup suggested calories result
        calorieNutrientService.setMockSuggestedCalories(2046);
        
        // Simulate user selecting calculate suggested calories (4) then entering details then saying no to setting as goal then exit
        String input = "4\nM\n30\n180\n75\n2\nN\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify calculate suggested calories was called
        assertEquals(1, calorieNutrientService.getCalculateSuggestedCaloriesCallCount());
        assertEquals('M', calorieNutrientService.getLastGender());
        assertEquals(30, calorieNutrientService.getLastAge());
        assertEquals(180, calorieNutrientService.getLastHeight(), 0.001);
        assertEquals(75, calorieNutrientService.getLastWeight(), 0.001);
        assertEquals(2, calorieNutrientService.getLastActivityLevel());
        
        // Verify no nutrition goals were set
        assertEquals(0, calorieNutrientService.getSetNutritionGoalsCallCount());
        
        // Verify result was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Your suggested daily calorie intake is: 2046 calories"));
    }
    
    @Test
    public void testCalculateSuggestedCaloriesAndSetAsGoal() {
        // Setup suggested calories result
        calorieNutrientService.setMockSuggestedCalories(2046);
        
        // Simulate user selecting calculate suggested calories (4) then entering details then saying yes to setting as goal then exit
        String input = "4\nM\n30\n180\n75\n2\nY\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify nutrition goals were set
        assertEquals(1, calorieNutrientService.getSetNutritionGoalsCallCount());
        assertEquals("testuser", calorieNutrientService.getLastUsername());
        assertEquals(2046, calorieNutrientService.getLastCalorieGoal());
        
        // Verify macronutrient split: 25% protein, 50% carbs, 25% fat
        double expectedProtein = 2046 * 0.25 / 4;  // 25% protein, 4 calories per gram
        double expectedCarbs = 2046 * 0.5 / 4;     // 50% carbs, 4 calories per gram
        double expectedFat = 2046 * 0.25 / 9;      // 25% fat, 9 calories per gram
        
        assertEquals(expectedProtein, calorieNutrientService.getLastProteinGoal(), 0.001);
        assertEquals(expectedCarbs, calorieNutrientService.getLastCarbGoal(), 0.001);
        assertEquals(expectedFat, calorieNutrientService.getLastFatGoal(), 0.001);
    }
    
    @Test
    public void testCalculateSuggestedCaloriesWithInvalidInputs() {
        // Setup suggested calories result
        calorieNutrientService.setMockSuggestedCalories(2046);
        
        // Simulate user entering invalid inputs
        String input = "4\nX\nM\n-30\n30\n-180\n180\n-75\n75\n0\n2\nN\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error messages were displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input. Please enter M for male or F for female."));
        assertTrue(output.contains("Please enter a valid age between 1 and 120."));
        assertTrue(output.contains("Height must be positive."));
        assertTrue(output.contains("Weight must be positive."));
        assertTrue(output.contains("Please enter a number between 1 and 5."));
    }
    
    @Test
    public void testBrowseCommonFoods() {
        // Setup common foods
        setupMockCommonFoods();
        
        // Simulate user selecting browse common foods (5) then exit
        String input = "5\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify get common foods was called
        assertEquals(1, calorieNutrientService.getGetCommonFoodsWithNutrientsCallCount());
        
        // Verify foods were displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Common Foods with Nutrients"));
        assertTrue(output.contains("1. Apple (100.0g)"));
        assertTrue(output.contains("   Calories: 52"));
        assertTrue(output.contains("   Protein: 0.3g"));
        assertTrue(output.contains("2. Banana (100.0g)"));
    }
    
    @Test
    public void testGetDateInputFebruary() {
        // Test for February in a leap year
        String input = "2\n2024\n2\n29\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify correct date was used
        assertEquals("2024-02-29", calorieNutrientService.getLastDate());
    }
    
    @Test
    public void testGetDateInputFebruaryNonLeapYear() {
        // Test for February in a non-leap year
        String input = "2\n2023\n2\n29\n28\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify error message was displayed for day 29
        String output = outputStream.toString();
        assertTrue(output.contains("Please enter a valid day between 1 and 28."));
        
        // Verify correct date was used
        assertEquals("2023-02-28", calorieNutrientService.getLastDate());
    }
    
    @Test
    public void testGenerateWeekDatesFunction() {
        // Testing the generateWeekDates private method indirectly through weekly report
        setupMockWeeklyReport();
        
        // Simulate user selecting view weekly report (3) then entering start date then exit
        String input = "3\n2023\n4\n15\n\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create menu
        menu = new CalorieNutrientTrackingMenu(
            calorieNutrientService, mealPlanningService, authService, scanner);
        
        // Display menu
        menu.displayMenu();
        
        // Verify weekly report dates were generated correctly
        String[] expectedDates = new String[7];
        expectedDates[0] = "2023-04-15";
        expectedDates[1] = "2023-04-16";
        expectedDates[2] = "2023-04-17";
        expectedDates[3] = "2023-04-18";
        expectedDates[4] = "2023-04-19";
        expectedDates[5] = "2023-04-20";
        expectedDates[6] = "2023-04-21";
        
        String[] actualDates = calorieNutrientService.getLastDatesArray();
        assertArrayEquals(expectedDates, actualDates);
    }
    
    // Helper methods for setting up mock data
    
    private void setupMockNutritionReport() {
        // Create mock goals
        CalorieNutrientTrackingService.NutritionGoal goals = 
            calorieNutrientService.new NutritionGoal(2000, 50, 250, 70);
        
        // Create mock report
        CalorieNutrientTrackingService.NutritionReport report = 
            calorieNutrientService.new NutritionReport(
                "2023-04-15", 1500, 40, 200, 50, 20, 30, 1500, goals);
        
        // Set the mock report
        calorieNutrientService.setMockNutritionReport(report);
    }
    
    private void setupMockWeeklyReport() {
        List<CalorieNutrientTrackingService.NutritionReport> reports = new ArrayList<>();
        
        // Create mock goals
        CalorieNutrientTrackingService.NutritionGoal goals = 
            calorieNutrientService.new NutritionGoal(2000, 50, 250, 70);
        
        // Create reports for 7 days
        for (int i = 0; i < 7; i++) {
            String date = String.format("2023-04-%d", 15 + i);
            CalorieNutrientTrackingService.NutritionReport report = 
                calorieNutrientService.new NutritionReport(
                    date, 1500 - (i * 100), 40 - (i * 2), 200 - (i * 10), 
                    50 - (i * 3), 20, 30, 1500, goals);
            reports.add(report);
        }
        
        // Set the mock weekly report
        calorieNutrientService.setMockWeeklyReport(reports);
    }
    
    private void setupMockCommonFoods() {
        FoodNutrient[] commonFoods = new FoodNutrient[2];
        
        commonFoods[0] = new FoodNutrient(
            "Apple", 100.0, 52, 0.3, 14.0, 0.2, 2.4, 10.3, 1.0);
            
        commonFoods[1] = new FoodNutrient(
            "Banana", 100.0, 89, 1.1, 22.8, 0.3, 2.6, 12.2, 1.0);
        
        // Set the mock common foods
        calorieNutrientService.setMockCommonFoods(commonFoods);
    }
    
    /**
     * Mock implementation of CalorieNutrientTrackingService for testing purposes.
     */
    private static class MockCalorieNutrientTrackingService extends CalorieNutrientTrackingService {
        // Counters for method calls
        private int setNutritionGoalsCallCount = 0;
        private int getNutritionReportCallCount = 0;
        private int getWeeklyReportCallCount = 0;
        private int calculateSuggestedCaloriesCallCount = 0;
        private int getCommonFoodsWithNutrientsCallCount = 0;
        
        // Parameters passed to methods
        private String lastUsername;
        private int lastCalorieGoal;
        private double lastProteinGoal;
        private double lastCarbGoal;
        private double lastFatGoal;
        private String lastDate;
        private String[] lastDatesArray;
        private char lastGender;
        private int lastAge;
        private double lastHeight;
        private double lastWeight;
        private int lastActivityLevel;
        
        // Mock responses
        private NutritionReport mockNutritionReport;
        private List<NutritionReport> mockWeeklyReport;
        private int mockSuggestedCalories;
        private FoodNutrient[] mockCommonFoods;
        
        public MockCalorieNutrientTrackingService() {
            // Call super constructor with null (not used in tests)
            super(null);
        }
        
        // Getters for call counters
        public int getSetNutritionGoalsCallCount() { return setNutritionGoalsCallCount; }
        public int getGetNutritionReportCallCount() { return getNutritionReportCallCount; }
        public int getGetWeeklyReportCallCount() { return getWeeklyReportCallCount; }
        public int getCalculateSuggestedCaloriesCallCount() { return calculateSuggestedCaloriesCallCount; }
        public int getGetCommonFoodsWithNutrientsCallCount() { return getCommonFoodsWithNutrientsCallCount; }
        
        // Total functions called
        public int functionsCalledCount() {
            return setNutritionGoalsCallCount + getNutritionReportCallCount + 
                   getWeeklyReportCallCount + calculateSuggestedCaloriesCallCount + 
                   getCommonFoodsWithNutrientsCallCount;
        }
        
        // Getters for parameters
        public String getLastUsername() { return lastUsername; }
        public int getLastCalorieGoal() { return lastCalorieGoal; }
        public double getLastProteinGoal() { return lastProteinGoal; }
        public double getLastCarbGoal() { return lastCarbGoal; }
        public double getLastFatGoal() { return lastFatGoal; }
        public String getLastDate() { return lastDate; }
        public String[] getLastDatesArray() { return lastDatesArray; }
        public char getLastGender() { return lastGender; }
        public int getLastAge() { return lastAge; }
        public double getLastHeight() { return lastHeight; }
        public double getLastWeight() { return lastWeight; }
        public int getLastActivityLevel() { return lastActivityLevel; }
        
        // Setters for mock responses
        public void setMockNutritionReport(NutritionReport report) { 
            this.mockNutritionReport = report; 
        }
        
        public void setMockWeeklyReport(List<NutritionReport> reports) { 
            this.mockWeeklyReport = reports; 
        }
        
        public void setMockSuggestedCalories(int calories) { 
            this.mockSuggestedCalories = calories; 
        }
        
        public void setMockCommonFoods(FoodNutrient[] foods) { 
            this.mockCommonFoods = foods; 
        }
        
        // Override methods to track calls and parameters
        
        @Override
        public boolean setNutritionGoals(String username, int calorieGoal, double proteinGoal, 
                                        double carbGoal, double fatGoal) {
            setNutritionGoalsCallCount++;
            this.lastUsername = username;
            this.lastCalorieGoal = calorieGoal;
            this.lastProteinGoal = proteinGoal;
            this.lastCarbGoal = carbGoal;
            this.lastFatGoal = fatGoal;
            return true; // Always succeed in test
        }
        
        @Override
        public NutritionGoal getNutritionGoals(String username) {
            return new NutritionGoal(2000, 50, 250, 70); // Default test goals
        }
        
        @Override
        public NutritionReport getNutritionReport(String username, String date) {
            getNutritionReportCallCount++;
            this.lastUsername = username;
            this.lastDate = date;
            return mockNutritionReport != null ? mockNutritionReport : 
                new NutritionReport(date, 0, 0, 0, 0, 0, 0, 0, getNutritionGoals(username));
        }
        
        @Override
        public List<NutritionReport> getWeeklyReport(String username, String[] dates) {
            getWeeklyReportCallCount++;
            this.lastUsername = username;
            this.lastDatesArray = dates;
            return mockWeeklyReport != null ? mockWeeklyReport : new ArrayList<>();
        }
        
        @Override
        public int calculateSuggestedCalories(char gender, int age, double heightCm, 
                                            double weightKg, int activityLevel) {
            calculateSuggestedCaloriesCallCount++;
            this.lastGender = gender;
            this.lastAge = age;
            this.lastHeight = heightCm;
            this.lastWeight = weightKg;
            this.lastActivityLevel = activityLevel;
            return mockSuggestedCalories;
        }
        
        @Override
        public FoodNutrient[] getCommonFoodsWithNutrients() {
            getCommonFoodsWithNutrientsCallCount++;
            return mockCommonFoods != null ? mockCommonFoods : new FoodNutrient[0];
        }
    } 
    
    /**
     * Mock implementation of MealPlanningService for testing purposes.
     */
    private static class MockMealPlanningService extends MealPlanningService {
        public MockMealPlanningService() {
            // Call super constructor with null (not used in tests)
            super();
        }
    }
    
    /**
     * Mock implementation of AuthenticationService for testing purposes.
     */
    private static class MockAuthenticationService extends AuthenticationService {
        public MockAuthenticationService() {
            // Call super constructor with null (not used in tests)
            super();
        }
        
        @Override
        public User getCurrentUser() {
            // Create a simple object that matches what the menu needs
            return new User() {
                @Override
                public String getUsername() {
                    return "testuser"; 
                }
            };
        }
    }
}