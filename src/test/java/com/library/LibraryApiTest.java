package com.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book.BookRepository;
import com.library.book.IsbnRecordRepository;
import com.library.borrower.BorrowerRepository;
import com.library.loan.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LibraryApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private IsbnRecordRepository isbnRecordRepository;

    @BeforeEach
    void cleanDatabase() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        borrowerRepository.deleteAll();
        isbnRecordRepository.deleteAll();
    }

    @Test
    void publishesSwaggerDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Library API"))
                .andExpect(jsonPath("$.paths['/api/borrowers'].get").exists())
                .andExpect(jsonPath("$.paths['/api/isbns'].get").exists())
                .andExpect(jsonPath("$.paths['/api/books']").exists())
                .andExpect(jsonPath("$.paths['/api/books/{bookId}/borrow']").exists())
                .andExpect(jsonPath("$.paths['/api/books/{bookId}/return']").exists());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void registersCopiesLoansAndReturnsThem() throws Exception {
        long ada = registerBorrower("Ada Lovelace", "Ada@Example.com");
        long grace = registerBorrower("Grace Hopper", "grace@example.com");

        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com"}
                                """))
                .andExpect(status().isConflict());

        long firstCopy = registerBook("978-0-13-468599-1", "Effective   Java", "Joshua Bloch");
        long secondCopy = registerBook("0-13-468599-7", "Effective Java", "Joshua Bloch");
        assertNotEquals(firstCopy, secondCopy);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isbn":"9780134685991","title":"Effective Java","author":"Someone Else"}
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + ada + ")].email").value(contains("ada@example.com")))
                .andExpect(jsonPath("$[?(@.id == " + grace + ")].name").value(contains("Grace Hopper")));

        mockMvc.perform(get("/api/isbns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isbn").value("9780134685991"))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"));

        mockMvc.perform(post("/api/books/" + firstCopy + "/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerId\":" + ada + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(firstCopy))
                .andExpect(jsonPath("$.borrowerId").value(ada))
                .andExpect(jsonPath("$.returnedAt").value(nullValue()));

        mockMvc.perform(post("/api/books/" + firstCopy + "/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerId\":" + grace + "}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + firstCopy + ")].status").value(contains("BORROWED")))
                .andExpect(jsonPath("$[?(@.id == " + firstCopy + ")].borrowedBy.email").value(contains("ada@example.com")))
                .andExpect(jsonPath("$[?(@.id == " + firstCopy + ")].title").value(contains("Effective Java")))
                .andExpect(jsonPath("$[?(@.id == " + secondCopy + ")].status").value(contains("AVAILABLE")))
                .andExpect(jsonPath("$[?(@.id == " + firstCopy + ")].isbn").value(contains("9780134685991")));

        mockMvc.perform(post("/api/books/" + firstCopy + "/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerId\":" + grace + "}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/books/" + firstCopy + "/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerId\":" + ada + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedAt").exists());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + firstCopy + ")].status").value(contains("AVAILABLE")));
    }

    @Test
    void rejectsInvalidInputAndUnknownIds() throws Exception {
        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isbn":"9780134685992","title":"Bad","author":"Checksum"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/books/99/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerId\":1}"))
                .andExpect(status().isNotFound());
    }

    private long registerBorrower(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s"}
                                """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email.toLowerCase()))
                .andReturn();
        return readId(result);
    }

    private long registerBook(String isbn, String title, String author) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isbn":"%s","title":"%s","author":"%s"}
                                """.formatted(isbn, title, author)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andReturn();
        return readId(result);
    }

    private long readId(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
