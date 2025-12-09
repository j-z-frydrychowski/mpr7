import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testy funkcjonalności wyszukiwania na Wikipedii")
class WikipediaSearchTest extends BaseTest {

    // STAŁE LOKATORY (Wymaganie recenzji)
    private static final By SEARCH_INPUT = By.name("search");
    private static final By ARTICLE_HEADING = By.id("firstHeading");
    private static final By SEARCH_SUGGESTIONS = By.cssSelector(".cdx-menu-item bdi");
    private static final By SEARCH_RESULTS_HEADING = By.cssSelector(".mw-search-nonefound");

    @Test
    @DisplayName("Pole wyszukiwania powinno być widoczne na stronie głównej")
    void shouldDisplaySearchFieldWhenOnHomePage() {
        // Arrange
        driver.get("https://pl.wikipedia.org");

        // Act
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));

        // Assert
        assertThat(searchField.isDisplayed())
                .as("Pole wyszukiwania musi być widoczne")
                .isTrue();
    }

    @Test
    @DisplayName("Wyszukanie istniejącego hasła powinno przekierować do artykułu")
    void shouldRedirectToArticleWhenSearchingForExistingTerm() {
        // Arrange
        driver.get("https://pl.wikipedia.org");
        String term = "Java";

        // Act
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        searchField.sendKeys(term);
        searchField.sendKeys(Keys.ENTER);

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING));

        // Assert
        assertThat(heading.getText())
                .as("Tytuł artykułu powinien zawierać szukaną frazę")
                .contains(term);
    }

    @Test
    @DisplayName("Wpisanie fragmentu tekstu powinno wyświetlić podpowiedzi")
    void shouldDisplaySuggestionsWhenTypingPartialText() {
        // Arrange
        driver.get("https://pl.wikipedia.org");
        String partialText = "Warsz";

        // Act
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        searchField.sendKeys(partialText);

        // Czekamy na pojawienie się listy (AJAX)
        wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_SUGGESTIONS));
        List<WebElement> suggestions = driver.findElements(SEARCH_SUGGESTIONS);

        // Assert
        assertThat(suggestions)
                .as("Lista podpowiedzi nie może być pusta")
                .isNotEmpty()
                .extracting(WebElement::getText)
                .anyMatch(text -> text.contains("Warszawa"));
    }

    @Test
    @DisplayName("Wyszukanie nieistniejącej frazy powinno pokazać brak wyników")
    void shouldShowNoResultsMessageWhenSearchingForGibberish() {
        // Arrange
        driver.get("https://pl.wikipedia.org");
        String gibberish = "sdflkjghsdkjfghlskdjfg";

        // Act
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        searchField.sendKeys(gibberish);
        searchField.sendKeys(Keys.ENTER);

        // Czekamy na komunikat o braku wyników
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULTS_HEADING));

        // Assert
        assertThat(errorMsg.getText())
                .as("Powinien pojawić się komunikat o braku wyników")
                .contains("Nie znaleziono");
    }
}