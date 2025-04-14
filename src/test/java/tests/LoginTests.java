package tests; // Declares the package the class belongs to

// Import Playwright classes for browser automation
import com.microsoft.playwright.*;

// Import JUnit 5 testing annotations and assertions
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*; // Enables use of assertTrue and other assertions without class prefix

// Public class containing the login tests
public class LoginTests {
    // Static variables so they are shared across all tests
    static Playwright playwright; // Core object used to create browsers and contexts
    static Browser browser; // Represents the browser instance

    // Instance variables used in each test
    BrowserContext context; // A new browser context (isolated session like incognito)
    Page page; // A single tab or page inside the context

    // 👇 Toggle this to enable/disable pauses globally
    static boolean debug = true;
    void debugPause() {
        if (debug) {
            page.waitForTimeout(5000);
        }
    }

    // Runs once before all tests — sets up the Playwright and browser instance
    @BeforeAll
    static void setupAll() {
        playwright = Playwright.create(); // Start Playwright engine
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)); // Launch Chromium browser in non-headless mode (visible)
    }

    // Runs before each test — opens a new isolated browser context and page
    @BeforeEach
    void setup() {
        context = browser.newContext(); // Create a new browser context (separate cookies, sessions, etc.)
        page = context.newPage(); // Open a new tab/page in the context
    }

    // Test case: successful login with valid credentials
    @Test
    void successfulLogin() {
        page.navigate("https://the-internet.herokuapp.com/login"); // Go to the login page
        page.fill("#username", "tomsmith"); // Fill in username field
        page.fill("#password", "SuperSecretPassword!"); // Fill in correct password
        page.click("button[type='submit']"); // Click the login button

        Locator successMessage = page.locator(".flash.success"); // Locate the success message element
        successMessage.waitFor(); // Wait for it to appear

        assertTrue(successMessage.isVisible(), "Success message should appear"); // Assert that the success message is shown
    }

    // Test case: login attempt with correct username but wrong password
    @Test
    void loginWithWrongPasswordShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login"); // Navigate to the login page
        page.fill("#username", "tomsmith"); // Enter valid username
        page.fill("#password", "WrongPassword"); // Enter incorrect password
        page.click("button[type='submit']"); // Click login

        Locator errorMessage = page.locator(".flash.error"); // Locate the error message
        errorMessage.waitFor(); // Wait for it to appear

        // Check if error message text includes expected phrase
        assertTrue(errorMessage.textContent().contains("Your password is invalid!"),
                "Should show invalid password error");
    }

    // Test case: login attempt with both fields empty
    @Test
    void loginWithEmptyFieldsShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login"); // Navigate to the login page
        page.click("button[type='submit']"); // Click login without entering any credentials

        Locator errorMessage = page.locator(".flash.error"); // Locate the error message
        errorMessage.waitFor(); // Wait for it to appear

        assertTrue(errorMessage.isVisible(), "Should show error for empty fields"); // Verify error is shown
    }

    // Test case: login attempt with incorrect username but correct password
    @Test
    void loginWithInvalidUsernameShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login"); // Navigate to the login page
        page.fill("#username", "invalidUser"); // Enter invalid username
        page.fill("#password", "SuperSecretPassword!"); // Enter correct password
        page.click("button[type='submit']"); // Click login

        Locator errorMessage = page.locator(".flash.error"); // Locate the error message
        errorMessage.waitFor(); // Wait for it to appear

        assertTrue(errorMessage.isVisible(), "Should show error for invalid username"); // Confirm error is displayed
    }

    // Runs after each test — closes the browser context (cleans up session)
    @AfterEach
    void tearDown() {
        debugPause(); // now clean and reusable
        context.close();
    }

    // Runs once after all tests — closes the browser and Playwright engine
    @AfterAll
    static void tearDownAll() {
        browser.close(); // Close the browser instance
        playwright.close(); // Shut down the Playwright engine
    }
}

