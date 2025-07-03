package com.nosbor.reviewer.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

/**
 * Represents the top-level response object from the Gemini API.
 * The @JsonIgnoreProperties annotation makes parsing robust by ignoring any new fields
 * the API might add in the future, preventing your code from breaking.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponseTO(
        List<Candidate> candidates,
        UsageMetadata usageMetadata
) {

    /**
     * A convenient helper method to safely extract the text from the first candidate.
     * In most standard use cases, you'll only care about the first candidate's content.
     *
     * @return An Optional containing the text, or an empty Optional if no content is available.
     */
    public Optional<String> getFirstCandidateText() {
        return Optional.ofNullable(candidates)
                .flatMap(cands -> cands.stream().findFirst())
                .map(Candidate::content)
                .map(Content::parts)
                .flatMap(parts -> parts.stream().findFirst())
                .map(Part::text);
    }
}

/**
 * A response candidate, containing the generated content and safety ratings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record Candidate(
        Content content,
        String finishReason,
        List<SafetyRating> safetyRatings
) {}

/**
 * The actual content, which consists of one or more parts.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record Content(
        List<Part> parts,
        String role
) {}

/**
 * A single part of the content, which holds the generated text.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record Part(
        String text
) {}

/**
 * Information about the token usage for the request and response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record UsageMetadata(
        Integer promptTokenCount,
        @JsonProperty("candidatesTokenCount") // The JSON property is plural
        Integer candidatesTokenCount,
        Integer totalTokenCount
) {}

/**
 * Safety rating for a specific category.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record SafetyRating(
        String category,
        String probability
) {}