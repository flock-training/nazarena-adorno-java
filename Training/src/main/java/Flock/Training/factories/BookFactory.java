package Flock.Training.factories;

import Flock.Training.dtos.BookInfoDTO;
import Flock.Training.models.Book;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BookFactory {
    public Book createBook(BookInfoDTO dto) {
        return Book.builder()
                .genre("")
                .author(dto.getAuthors().isEmpty() ? "" : String.join(", ", dto.getAuthors()))
                .image("")
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .publisher(dto.getPublisher())
                .year(dto.getPublishDate())
                .pages(dto.getNumberOfPages())
                .isbn(dto.getIsbn())
                .build();
    }

    public BookInfoDTO createBookDTO(JsonNode bookNode, String isbn) {
        String title = bookNode.path("title").asText("");
        String subtitle = bookNode.path("subtitle").asText("");
        List<String> authors = bookNode.path("authors").isArray()
                ? bookNode.path("authors").findValuesAsText("name")
                : Collections.emptyList();
        int pageCount = bookNode.path("number_of_pages").asInt(0);
        String publisher = bookNode.path("publishers").isArray()
                ? bookNode.path("publishers").get(0).path("name").asText("")
                : "";
        String publishDate = bookNode.path("publish_date").asText("");

        return new BookInfoDTO(isbn, title, subtitle, publisher, publishDate, pageCount, authors);
    }
}