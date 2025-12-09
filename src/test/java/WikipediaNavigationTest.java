import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testy nawigacji i struktury linków")
class WikipediaNavigationTest extends BaseTest {

    private static final By ARTICLE_CONTENT_LINKS = By.cssSelector("#mw-content-text p a[href^='/wiki/']");
    private static final By MAIN_HEADING = By.id("firstHeading");
    private static final By TABLE_OF_CONTENTS = By.id("vector-toc");
    private static final By MAIN_PAGE_LOGO = By.cssSelector("a.mw-logo");
    private static final By SIDEBAR_NAVIGATION = By.id("p-navigation");

    @Test
    @DisplayName("Kliknięcie w link w artykule powinno przenieść na nową podstronę")
    void shouldNavigateToNewPageWhenClickingArticleLink() {
        // Arrange
        driver.get("https://pl.wikipedia.org/wiki/Java");
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_HEADING));

        List<WebElement> links = driver.findElements(ARTICLE_CONTENT_LINKS);

        WebElement validLink = links.stream()
                .filter(l -> !l.getAttribute("href").contains(":"))
                .findFirst()
                .orElseThrow();

        String expectedUrl = validLink.getAttribute("href");

        // Act
        validLink.click();
        wait.until(ExpectedConditions.urlToBe(expectedUrl));

        // Assert
        assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("Długi artykuł powinien posiadać spis treści")
    void shouldDisplayTableOfContentsOnLongArticle() {
        // Arrange
        driver.get("https://pl.wikipedia.org/wiki/Java");
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_HEADING));

        // Act
        WebElement toc = wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE_OF_CONTENTS));

        // Assert
        assertThat(toc.isDisplayed())
                .as("Spis treści powinien być widoczny")
                .isTrue();
    }

    @Test
    @DisplayName("Kliknięcie w logo Wikipedii powinno przenieść na stronę główną")
    void shouldNavigateToMainPageWhenClickingLogo() {
        // Arrange
        driver.get("https://pl.wikipedia.org/wiki/Java");

        // Act
        WebElement logo = wait.until(ExpectedConditions.elementToBeClickable(MAIN_PAGE_LOGO));
        logo.click();

        wait.until(ExpectedConditions.titleContains("Wikipedia, wolna encyklopedia"));

        // Assert
        assertThat(driver.getCurrentUrl())
                .as("URL powinien wskazywać na stronę główną")
                .isEqualTo("https://pl.wikipedia.org/wiki/Wikipedia:Strona_g%C5%82%C3%B3wna");
    }

    @Test
    @DisplayName("Pasek boczny powinien zawierać menu nawigacyjne")
    void shouldDisplaySidebarNavigation() {
        // Arrange
        driver.get("https://pl.wikipedia.org");

        // Act
        WebElement sidebar = wait.until(ExpectedConditions.visibilityOfElementLocated(SIDEBAR_NAVIGATION));

        // Assert
        assertThat(sidebar.isDisplayed()).isTrue();
        assertThat(sidebar.getText()).contains("Strona główna", "Losuj artykuł");
    }
}