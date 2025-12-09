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

import static org.assertj.core.api.Assertions.assertThat;

class WikipediaContentTest {

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
    void articleShouldHaveCorrectTitleImageAndReferences() {
        // 1. Arrange
        String articleUrl = "https://pl.wikipedia.org/wiki/Java";
        String expectedTitleStart = "Java[edytuj wstęp]"; // Dokładny tytuł H1 na stronie "Java" w polskiej Wikipedii

        driver.get(articleUrl);

        // 2. Act
        // A. Czekamy na główny nagłówek
        WebElement mainHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading")));

        // B. Szukamy obrazka w Infoboxie
        WebElement infoboxImage = driver.findElement(By.cssSelector(".infobox img"));

        // C. Szukamy sekcji "Przypisy"
        WebElement referencesHeader = driver.findElement(By.id("Przypisy"));

        // 3. Assert

        // Weryfikacja 1: Tytuł H1
        assertThat(mainHeader.getText())
                .as("Nagłówek H1 powinien zgadzać się z tematem artykułu")
                .isEqualTo(expectedTitleStart);


        // Weryfikacja 2: Obrazek
        assertThat(infoboxImage.isDisplayed())
                .as("Logo/Maskotka w Infoboxie powinna być widoczna")
                .isTrue();

        assertThat(infoboxImage.getAttribute("src"))
                .as("Obrazek powinien przedstawiać maskotkę Java (Duke)")
                .contains("Duke");


        // Weryfikacja 3: Przypisy
        // Scrollujemy do przypisów, żeby upewnić się, że istnieją w strukturze
        assertThat(referencesHeader.isDisplayed())
                .as("Sekcja 'Przypisy' powinna być obecna w artykule")
                .isTrue();
    }
}