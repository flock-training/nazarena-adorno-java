package Flock.Training.services;

import Flock.Training.dtos.BookInfoDTO;
import Flock.Training.exceptions.ApiResponseException;
import Flock.Training.factories.BookFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Servicio para la búsqueda de libros en la API de Open Library
 */
@Service
public class OpenLibraryService {

    @Autowired
    private BookFactory bookFactory;

    private static final String BASE_URL = "https://openlibrary.org";
    private static final String BOOKS_ENDPOINT = "/api/books";
    private final WebClient webClient;

    public OpenLibraryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    /**
     * Busca la información de un libro en la API externa de Open Library utilizando su ISBN.
     * <p>
     * Si el libro se encuentra en la API, devuelve un {@link BookInfoDTO} con los datos obtenidos.
     * Si el libro no está disponible en la API o hay un error en la respuesta, devuelve {@code null}.
     * </p>
     *
     * @param isbn Código identificador del libro
     * @return Un {@link BookInfoDTO} con la información del libro si se encuentra, o {@code null} si no está disponible.
     * @throws ApiResponseException Si hubo un error en procesar la respuesta de la API.
     */
    public BookInfoDTO getBookInfo(String isbn) {
        String url = UriComponentsBuilder.fromPath(BOOKS_ENDPOINT)
                .queryParam("bibkeys", "ISBN:" + isbn)
                .queryParam("jscmd", "data")
                .queryParam("format", "json")
                .toUriString();


        String responseString = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (responseString == null || responseString.isBlank()) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseString);

            // Extraer el objeto del libro usando el ISBN como clave
            JsonNode bookNode = rootNode.get("ISBN:" + isbn);
            if (bookNode == null) {
                return null;
            }

            // Extraer los atributos necesarios, crear y devolver Book DTO
            return bookFactory.createBookDTO(bookNode, isbn);

        } catch (Exception e) {
            throw new ApiResponseException("Error al procesar la respuesta de la API", HttpStatus.BAD_GATEWAY, e);
        }
    }
}
