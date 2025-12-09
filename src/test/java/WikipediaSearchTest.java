import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WikipediaSearchTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();
        // Maksymalizacja okna, żeby widzieć wszystkie elementy
        driver.manage().window().maximize();
        // Inicjalizacja Waita raz, używamy go w testach
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        // "Sprzątanie" po teście
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void searchFieldShouldBeVisibleOnHomePage() {
        // 1. Arrange (Przygotowanie)
        driver.get("https://pl.wikipedia.org");

        // 2. Act (Działanie + Czekanie)
        // Czekamy na oczekiwany stan - element gotowy do interakcji
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("search")));

        // 3. Assert (Weryfikacja biznesowa)
        // Sprawdzamy czy element jest faktycznie wyświetlony
        assertThat(searchInput.isDisplayed())
                .as("Pole wyszukiwania powinno być widoczne na stronie głównej")
                .isTrue();

        // Dodatkowe sprawdzenie placeholder'a dla pewności, że to to pole
        assertThat(searchInput.getAttribute("placeholder"))
                .contains("Przeszukaj Wikipedię");
    }

    @Test
    void searchForExistingArticleShouldRedirectToArticlePage() {
        // 1. Arrange
        driver.get("https://pl.wikipedia.org");
        String searchQuery = "Java";

        //String searchQuery = "qweasdzxc123456";

        // 2. Act
        // Znajdź pole (czekaj aż będzie klikalne)
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("search")));

        // Wpisz frazę i zatwierdź ENTEREM (symulacja klawiatury)
        searchInput.sendKeys(searchQuery);
        searchInput.sendKeys(Keys.ENTER);

        // Czekaj aż nagłówek artykułu będzie widoczny - najpewniejsza możliwa opcja załadowania się strony
        WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading")));

        // 3. Assert
        // Sprawdzamy czy nagłówek zawiera słowo "Java"
        // Używamy .contains(), bo tytuł może brzmieć "Java (język programowania)" lub "Java (wyspa)"
        assertThat(pageHeader.getText())
                .as("Tytuł artykułu powinien zawierać wyszukiwaną frazę")
                .contains(searchQuery);
    }

    @Test
    void searchSuggestionsShouldAppearForPartialText() {
        // 1. Arrange
        driver.get("https://pl.wikipedia.org");
        String partialText = "Warsz";
        String expectedSuggestion = "Warszawa";

        // 2. Act
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("search")));
        searchInput.sendKeys(partialText);

        // Czekamy aż pojawi się lista podpowiedzi
        By suggestionSelector = By.cssSelector(".cdx-menu-item bdi");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionSelector));

        // Gdy lista jest załadowana pobieramy ją
        List<WebElement> suggestions = driver.findElements(suggestionSelector);

        // 3. Assert
        // Wyciągamy tekst z każdego elementu i sprawdzamy, czy Warszawa tam jest
        assertThat(suggestions)
                .as("Lista podpowiedzi powinna zawierać miasto Warszawa")
                .extracting(WebElement::getText) // Transformacja: WebElement -> String
                .contains(expectedSuggestion);
    }
}