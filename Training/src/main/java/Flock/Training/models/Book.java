package Flock.Training.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa la entidad 'Book' de la tabla 'Books'
 */
@Entity
@Table(name = "Books")
@Schema(name = "Book", description = "Representa un libro")
public class Book {

    /**
     * Atributos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Schema(description = "ID único del libro", example = "1")
    private Long id;

    @Schema(description = "Género literario del libro", example = "Narrativa")
    private String genre;

    @Schema(description = "Autor del libro", example = "George Eliot")
    private String author;

    @Schema(description = "URL de la imagen de portada del libro", example = "https://imagessl4.casadellibro.com/a/l/s7/94/9788484280194.webp")
    private String image;

    @Schema(description = "Título del libro", example = "Middlemarch")
    private String title;

    @Schema(description = "Título secundario con información adicional del libro", example = "Un estudio de la vida en provincias")
    private String subtitle;

    @Schema(description = "Editorial del libro", example = "Alba Editorial")
    private String publisher;

    @Schema(description = "Año de la edición del libro", example = "2000")
    private String year;

    @Schema(description = "Número de páginas del libro", example = "896")
    private int pages;

    /**
     * Código ISBN del libro.
     * <p>
     * Número que identifica de una manera única a cada libro.
     */
    @Schema(description = "Número identificador del libro", example = "9788484280194")
    private String isbn;

    /**
     * Constructor por defecto
     */
    public Book() {
    }

    public Book(String genre, String author, String image, String title, String subtitle, String publisher, String year, int pages, String isbn) {
        this.genre = genre;
        this.author = author;
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.isbn = isbn;
    }

    /**
     * Getters y Setters
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
