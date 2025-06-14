package models;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Borrow {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private Long userId;
	    private Long bookId;
	    private LocalDate borrowDate;
	    private LocalDate dueDate;
	    private String status;

	    public Borrow() {}
	    public Borrow(Long userId, Long bookId, LocalDate borrowDate, LocalDate dueDate, String status) {
	        this.userId = userId;
	        this.bookId = bookId;
	        this.borrowDate = borrowDate;
	        this.dueDate = dueDate;
	        this.status = status;
	    }

	    // Getters and Setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	    public Long getUserId() { return userId; }
	    public void setUserId(Long userId) { this.userId = userId; }
	    public Long getBookId() { return bookId; }
	    public void setBookId(Long bookId) { this.bookId = bookId; }
	    public LocalDate getBorrowDate() { return borrowDate; }
	    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
	    public LocalDate getDueDate() { return dueDate; }
	    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }

	    @Override
	    public String toString() {
	        return "Borrow{id=" + id + ", userId=" + userId + ", bookId=" + bookId +
	               ", borrowDate=" + borrowDate + ", dueDate=" + dueDate + ", status='" + status + "'}";
	    }
	}