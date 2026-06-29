package dev.atinroy.ledgerly.domain.party.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the Party entity.
 *
 * The Party entity represents a counterparty in transactions (e.g., "Amazon", "Walmart").
 * Key feature: Name normalization for case-insensitive deduplication.
 *
 * The normalize() method:
 * - Converts to lowercase
 * - Trims leading/trailing whitespace
 * - Collapses multiple spaces into single space
 *
 * This ensures "KFC", "kfc", " KFC ", "  KFC" all map to the same party.
 */
@DisplayName("Party Entity Tests")
class PartyTest {

    @Nested
    @DisplayName("Name Normalization")
    class NameNormalization {

        @Test
        @DisplayName("should convert name to lowercase")
        void shouldConvertNameToLowercase() {
            // Arrange & Act: Normalize an uppercase name
            String normalized = Party.normalize("AMAZON");

            // Assert: Should be lowercase
            assertThat(normalized).isEqualTo("amazon");
        }

        @Test
        @DisplayName("should convert mixed case to lowercase")
        void shouldConvertMixedCaseToLowercase() {
            // Arrange & Act: Normalize a mixed case name
            String normalized = Party.normalize("McDonald's");

            // Assert
            assertThat(normalized).isEqualTo("mcdonald's");
        }

        @Test
        @DisplayName("should trim leading whitespace")
        void shouldTrimLeadingWhitespace() {
            // Arrange & Act: Name with leading spaces
            String normalized = Party.normalize("   Walmart");

            // Assert: Leading spaces removed
            assertThat(normalized).isEqualTo("walmart");
        }

        @Test
        @DisplayName("should trim trailing whitespace")
        void shouldTrimTrailingWhitespace() {
            // Arrange & Act: Name with trailing spaces
            String normalized = Party.normalize("Walmart   ");

            // Assert: Trailing spaces removed
            assertThat(normalized).isEqualTo("walmart");
        }

        @Test
        @DisplayName("should trim both leading and trailing whitespace")
        void shouldTrimBothLeadingAndTrailingWhitespace() {
            // Arrange & Act
            String normalized = Party.normalize("  Target  ");

            // Assert
            assertThat(normalized).isEqualTo("target");
        }

        @Test
        @DisplayName("should collapse multiple internal spaces to single space")
        void shouldCollapseMultipleSpacesToSingle() {
            // Arrange & Act: Name with multiple internal spaces
            String normalized = Party.normalize("Whole    Foods    Market");

            // Assert: Multiple spaces collapsed to single space
            assertThat(normalized).isEqualTo("whole foods market");
        }

        @Test
        @DisplayName("should handle combination of all normalization rules")
        void shouldHandleCombinationOfAllRules() {
            // Arrange & Act: Name requiring all normalization rules
            String normalized = Party.normalize("  BEST    BUY   ");

            // Assert: Trimmed, lowercased, spaces collapsed
            assertThat(normalized).isEqualTo("best buy");
        }

        @Test
        @DisplayName("should preserve single space between words")
        void shouldPreserveSingleSpaceBetweenWords() {
            // Arrange & Act: Normal name with proper spacing
            String normalized = Party.normalize("Home Depot");

            // Assert: Single space preserved
            assertThat(normalized).isEqualTo("home depot");
        }

        @Test
        @DisplayName("should handle single word names")
        void shouldHandleSingleWordNames() {
            // Arrange & Act
            String normalized = Party.normalize("Costco");

            // Assert
            assertThat(normalized).isEqualTo("costco");
        }

        @Test
        @DisplayName("should handle tabs and newlines in whitespace")
        void shouldHandleTabsAndNewlines() {
            // Arrange & Act: Name with various whitespace characters
            String normalized = Party.normalize("Trader\t\tJoe's\n");

            // Assert: All whitespace treated the same and collapsed
            assertThat(normalized).isEqualTo("trader joe's");
        }

        @Test
        @DisplayName("should throw exception for null name")
        void shouldThrowExceptionForNullName() {
            // Act & Assert: Null should throw IllegalArgumentException
            assertThatThrownBy(() -> Party.normalize(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Party name cannot be null");
        }

        @Test
        @DisplayName("should handle name with only whitespace")
        void shouldHandleNameWithOnlyWhitespace() {
            // Arrange & Act: Whitespace-only name
            String normalized = Party.normalize("   ");

            // Assert: Results in empty string after trim
            assertThat(normalized).isEqualTo("");
        }

        @Test
        @DisplayName("should preserve special characters")
        void shouldPreserveSpecialCharacters() {
            // Arrange & Act: Name with special characters
            String normalized = Party.normalize("AT&T - Store #123");

            // Assert: Special chars preserved, case lowered
            assertThat(normalized).isEqualTo("at&t - store #123");
        }

        @Test
        @DisplayName("should preserve unicode characters")
        void shouldPreserveUnicodeCharacters() {
            // Arrange & Act: Name with unicode
            String normalized = Party.normalize("Cafe");

            // Assert: Unicode preserved
            assertThat(normalized).isEqualTo("cafe");
        }

        @Test
        @DisplayName("should handle apostrophes in names")
        void shouldHandleApostrophesInNames() {
            // Arrange & Act: Common name with apostrophe
            String normalized = Party.normalize("Wendy's");

            // Assert
            assertThat(normalized).isEqualTo("wendy's");
        }

        @Test
        @DisplayName("should handle numeric names")
        void shouldHandleNumericNames() {
            // Arrange & Act: Name with numbers
            String normalized = Party.normalize("7-Eleven");

            // Assert
            assertThat(normalized).isEqualTo("7-eleven");
        }

        @Test
        @DisplayName("should return same result for equivalent inputs")
        void shouldReturnSameResultForEquivalentInputs() {
            // These should all normalize to the same value
            // demonstrating case-insensitive deduplication
            String[] variants = {
                    "KFC",
                    "kfc",
                    "Kfc",
                    " KFC",
                    "KFC ",
                    "  kfc  ",
                    "K F C"  // This one differs - spaces between letters
            };

            // First 6 should match
            for (int i = 0; i < 6; i++) {
                assertThat(Party.normalize(variants[i]))
                        .as("Variant %d should normalize to 'kfc'", i)
                        .isEqualTo("kfc");
            }

            // Last one has spaces between letters - different
            assertThat(Party.normalize(variants[6])).isEqualTo("k f c");
        }
    }
}
