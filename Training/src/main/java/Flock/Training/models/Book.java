package Flock.Training.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Representa la entidad 'Book' de la tabla 'Books'
 */
@Entity
@Table(name = "Books")
@Schema(name = "Book", description = "Representa un libro")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
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
}
