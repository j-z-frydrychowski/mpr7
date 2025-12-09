import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WikipediaNavigationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void clickingArticleLinkShouldNavigateToNewPage() {
        // 1. Arrange
        driver.get("https://pl.wikipedia.org/wiki/Java");

        // Czekamy na treść
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading")));

        // 2. Act
        List<WebElement> articleLinks = driver.findElements(By.cssSelector("#mw-content-text p a[href^='/wiki/']"));

        WebElement linkToClick = articleLinks.stream()
                // FILTR POPRAWIONY: Ignorujemy dwukropek w "https:", szukamy go tylko w treści po "/wiki/"
                .filter(link -> {
                    String href = link.getAttribute("href");
                    // Zabezpieczenie na wypadek null (choć selektor to raczej wyklucza)
                    if (href == null) return false;
                    // Dzielimy URL na części przez "/wiki/" i sprawdzamy tylko drugą część (nazwę artykułu)
                    String[] parts = href.split("/wiki/");
                    return parts.length > 1 && !parts[1].contains(":");
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nie znaleziono żadnego poprawnego linku w artykule!"));

        String expectedUrl = linkToClick.getAttribute("href");

        // Na razie spróbujmy zwykłe kliknięcie:
        linkToClick.click();

        wait.until(ExpectedConditions.urlToBe(expectedUrl));

        // 3. Assert
        assertThat(driver.getCurrentUrl())
                .as("Po kliknięciu w link użytkownik powinien zostać przekierowany na nową podstronę")
                .isEqualTo(expectedUrl);
    }

    @Test
    void tableOfContentsShouldBePresentOnLongArticle() {
        // 1. Arrange
        driver.get("https://pl.wikipedia.org/wiki/Java");

        // 2. Act
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading")));

        // Lokalizator: ID > class
        By tocSelector = By.id("vector-toc");

        // Czekamy na widoczność spisu treści
        WebElement tableOfContents = wait.until(ExpectedConditions.visibilityOfElementLocated(tocSelector));

        // 3. Assert
        assertThat(tableOfContents.isDisplayed())
                .as("Spis treści powinien być widoczny dla długiego artykułu")
                .isTrue();

        // Opcjonalnie: Sprawdźmy, czy spis treści faktycznie ma jakieś punkty
        // Szukamy linków wewnątrz spisu treści
        assertThat(tableOfContents.findElements(By.tagName("a")))
                .as("Spis treści powinien zawierać linki do sekcji")
                .isNotEmpty(); // Lista linków nie może być pusta
    }
}