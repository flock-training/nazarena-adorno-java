package Flock.Training.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa información de la entidad 'Book'
 */
@Schema(name = "BookInfoDTO", description = "Representa información un libro")
public class BookInfoDTO {

    /**
     * Atributos
     */

    @Schema(description = "Número identificador del libro", example = "9788484280194")
    private String isbn;

    @Schema(description = "Título del libro", example = "Middlemarch")
    private String title;

    @Schema(description = "Título secundario con información adicional del libro", example = "Un estudio de la vida en provincias")
    private String subtitle;

    @Schema(description = "Editorial del libro", example = "Alba Editorial")
    private String publisher;

    @Schema(description = "Año de la edición del libro", example = "2000")
    private String publishDate;

    @Schema(description = "Número de páginas del libro", example = "896")
    private int numberOfPages;

    @Schema(description = "Autor/es del libro", example = "George Eliot")
    private List<String> authors = new ArrayList<>();


    /**
     * Constructores
     */
    public BookInfoDTO() {
    }

    public BookInfoDTO(String isbn, String title, String subtitle, String publisher,
                       String publishDate, int numberOfPages, List<String> authors) {
        this.isbn = isbn;
        this.title = title;
        this.subtitle = subtitle;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.numberOfPages = numberOfPages;
        this.authors = authors;
    }

    /**
     * Getters y Setters
     */
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
