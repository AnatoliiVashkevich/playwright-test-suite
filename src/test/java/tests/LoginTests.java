package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void successfulLogin() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        page.click("button[type='submit']");

        Locator successMessage = page.locator(".flash.success");
        successMessage.waitFor(); // Wait until it appears

        assertTrue(successMessage.isVisible(), "Success message should appear");
    }

    @Test
    void loginWithWrongPasswordShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "tomsmith");
        page.fill("#password", "WrongPassword");
        page.click("button[type='submit']");

        Locator errorMessage = page.locator(".flash.error");
        errorMessage.waitFor();

        assertTrue(errorMessage.textContent().contains("Your password is invalid!"),
                "Should show invalid password error");
    }

    @Test
    void loginWithEmptyFieldsShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.click("button[type='submit']");

        Locator errorMessage = page.locator(".flash.error");
        errorMessage.waitFor();

        assertTrue(errorMessage.isVisible(), "Should show error for empty fields");
    }

    @Test
    void loginWithInvalidUsernameShowsError() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "invalidUser");
        page.fill("#password", "SuperSecretPassword!");
        page.click("button[type='submit']");

        Locator errorMessage = page.locator(".flash.error");
        errorMessage.waitFor();

        assertTrue(errorMessage.isVisible(), "Should show error for invalid username");
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @AfterAll
    static void tearDownAll() {
        browser.close();
        playwright.close();
    }
}