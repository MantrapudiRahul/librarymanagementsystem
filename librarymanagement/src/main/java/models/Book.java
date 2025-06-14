package models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String title;
	    private String author;
	    private String isbn;
	    private int publicationYear;
	    private int availableCopies;

	    public Book() {}
	    public Book(String title, String author, String isbn, int publicationYear, int availableCopies) {
	        this.title = title;
	        this.author = author;
	        this.isbn = isbn;
	        this.publicationYear = publicationYear;
	        this.availableCopies = availableCopies;
	    }

	    // Getters and Setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	    public String getTitle() { return title; }
	    public void setTitle(String title) { this.title = title; }
	    public String getAuthor() { return author; }
	    public void setAuthor(String author) { this.author = author; }
	    public String getIsbn() { return isbn; }
	    public void setIsbn(String isbn) { this.isbn = isbn; }
	    public int getPublicationYear() { return publicationYear; }
	    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
	    public int getAvailableCopies() { return availableCopies; }
	    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

	    @Override
	    public String toString() {
	        return "Book{id=" + id + ", title='" + title + "', author='" + author + "', isbn='" + isbn +
	               "', publicationYear=" + publicationYear + ", availableCopies=" + availableCopies + "}";
	    }
	}
