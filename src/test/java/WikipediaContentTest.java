import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testy weryfikacji treści artykułu (Java)")
class WikipediaContentTest extends BaseTest {

    private static final String JAVA_ARTICLE_URL = "https://pl.wikipedia.org/wiki/Java";

    private static final By MAIN_HEADING = By.id("firstHeading");
    private static final By INFOBOX_IMAGE = By.cssSelector(".infobox img");
    private static final By REFERENCES_SECTION = By.id("Przypisy");
    private static final By FOOTER_COPYRIGHT = By.id("footer-info-copyright");

    @Test
    @DisplayName("Nagłówek H1 powinien zgadzać się z tytułem artykułu")
    void shouldDisplayCorrectTitleForJavaArticle() {
        // Arrange
        driver.get(JAVA_ARTICLE_URL);

        // Act
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_HEADING));

        // Assert
        assertThat(heading.getText())
                .as("Tytuł artykułu jest niepoprawny")
                .contains("Java (język programowania)");
    }

    @Test
    @DisplayName("Infobox powinien zawierać zdjęcie maskotki (Duke)")
    void shouldDisplayDukeMascotImageInInfobox() {
        // Arrange
        driver.get(JAVA_ARTICLE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_HEADING));

        // Act
        WebElement image = driver.findElement(INFOBOX_IMAGE);

        // Assert
        assertThat(image.isDisplayed()).isTrue();
        assertThat(image.getAttribute("src"))
                .as("Obrazek powinien być plikiem z maskotką Duke")
                .contains("Duke");
    }

    @Test
    @DisplayName("Artykuł powinien posiadać sekcję Przypisy")
    void shouldDisplayReferencesSectionInArticle() {
        // Arrange
        driver.get(JAVA_ARTICLE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_HEADING));

        // Act
        WebElement references = driver.findElement(REFERENCES_SECTION);

        // Assert
        assertThat(references.isDisplayed())
                .as("Sekcja przypisów powinna być widoczna")
                .isTrue();
    }

    @Test
    @DisplayName("Stopka powinna zawierać informację o licencji")
    void shouldDisplayCopyrightInfoInFooter() {
        // Arrange
        driver.get(JAVA_ARTICLE_URL);

        // Act
        // Scrollujemy na dół
        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(FOOTER_COPYRIGHT));

        // Assert
        assertThat(footer.getText())
                .as("Stopka powinna wspominać o licencji Creative Commons")
                .contains("Creative Commons");
    }
}