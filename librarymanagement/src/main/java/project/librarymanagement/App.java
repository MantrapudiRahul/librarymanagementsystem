package project.librarymanagement;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import dao.AdminDAO;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.UserDAO;
import models.Admin;
import models.Book;
import models.Borrow;
import models.User;
import util.HibernateUtil;

public class App {

	private static final BookDAO bookDAO = new BookDAO();
	private static final UserDAO userDAO = new UserDAO();
	private static final BorrowDAO borrowDAO = new BorrowDAO();
	private static final AdminDAO adminDAO = new AdminDAO();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
	        try {
	          if (!login()) {
	               System.out.println("Exiting due to failed login attempts.");
	               return;
	          }
	

	            while (true) {
	                System.out.println("\nLibrary Management System");
	                System.out.println("1. Add Book");
	                System.out.println("2. View Books");
	                System.out.println("3. Update Book");
	                System.out.println("4. Delete Book");
	                System.out.println("5. Register User");
	                System.out.println("6. View Users");
	                System.out.println("7. Borrow Book");
	                System.out.println("8. Return Book");
	            System.out.println("9. View Borrowings");
	            System.out.println("10. Exit");
	            System.out.print("Enter choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume newline

	            switch (choice) {
	                case 1:
	                    addBook();
	                    break;
	                case 2:
	                    viewBooks();
	                    break;
	                case 3:
	                    updateBook();
	                    break;
	                case 4:
	                    deleteBook();
	                    break;
	                case 5:
	                    registerUser();
	                    break;
	                case 6:
	                    viewUsers();
	                    break;
	                case 7:
	                    borrowBook();
	                    break;
	                case 8:
	                    returnBook();
	                    break;
	                case 9:
	                    viewBorrowings();
	                    break;
	                case 10:
	                    HibernateUtil.shutdown();
	                    System.out.println("Exiting...");
	                    return;
	                default:
	                    System.out.println("Invalid choice!");
	                    break;
	            }
	            }
	            }catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            scanner.close();
	        }
	    }

	private static boolean login() {
		int attempts = 3;
		while (attempts > 0) {
			System.out.println("\nAdmin Login");
			System.out.print("Enter username: ");
			String username = scanner.nextLine();
			System.out.print("Enter password: ");
			String password = scanner.nextLine();

			Admin admin = adminDAO.findByUsernameAndPassword(username, password);
			if (admin != null) {
				System.out.println("Login successful!");
				return true;
			} else {
				attempts--;
				System.out.println("Invalid credentials. " + attempts + " attempts remaining.");
			}
		}
		return false;
	}

	private static void addBook() {
		System.out.print("Enter title: ");
		String title = scanner.nextLine();
		System.out.print("Enter author: ");
		String author = scanner.nextLine();
		System.out.print("Enter ISBN: ");
		String isbn = scanner.nextLine();
		System.out.print("Enter publication year: ");
		int year = scanner.nextInt();
		System.out.print("Enter available copies: ");
		int copies = scanner.nextInt();
		scanner.nextLine();

		Book book = new Book(title, author, isbn, year, copies);
		bookDAO.save(book);
		System.out.println("Book added successfully!");
	}

	private static void viewBooks() {
		List<Book> books = bookDAO.findAll();
		if (books.isEmpty()) {
			System.out.println("No books found.");
		} else {
			books.forEach(System.out::println);
		}
	}

	private static void updateBook() {
		System.out.print("Enter book ID: ");
		Long id = scanner.nextLong();
		scanner.nextLine();
		Book book = bookDAO.findById(id);
		if (book == null) {
			System.out.println("Book not found!");
			return;
		}

		System.out.print("Enter new title (leave blank to keep '" + book.getTitle() + "'): ");
		String title = scanner.nextLine();
		if (!title.isEmpty())
			book.setTitle(title);

		System.out.print("Enter new author (leave blank to keep '" + book.getAuthor() + "'): ");
		String author = scanner.nextLine();
		if (!author.isEmpty())
			book.setAuthor(author);

		System.out.print("Enter new ISBN (leave blank to keep '" + book.getIsbn() + "'): ");
		String isbn = scanner.nextLine();
		if (!isbn.isEmpty())
			book.setIsbn(isbn);

		System.out.print("Enter new publication year (0 to keep " + book.getPublicationYear() + "): ");
		int year = scanner.nextInt();
		if (year != 0)
			book.setPublicationYear(year);

		System.out.print("Enter new available copies (0 to keep " + book.getAvailableCopies() + "): ");
		int copies = scanner.nextInt();
		scanner.nextLine();
		if (copies != 0)
			book.setAvailableCopies(copies);

		bookDAO.update(book);
		System.out.println("Book updated successfully!");
	}

	private static void deleteBook() {
		System.out.print("Enter book ID: ");
		Long id = scanner.nextLong();
		scanner.nextLine();
		bookDAO.delete(id);
		System.out.println("Book deleted successfully!");
	}

	private static void registerUser() {
		System.out.print("Enter name: ");
		String name = scanner.nextLine();
		System.out.print("Enter email: ");
		String email = scanner.nextLine();
		System.out.print("Enter phone number: ");
		String phone = scanner.nextLine();

		User user = new User(name, email, phone);
		userDAO.save(user);
		System.out.println("User registered successfully!");
	}

	private static void viewUsers() {
		List<User> users = userDAO.findAll();
		if (users.isEmpty()) {
			System.out.println("No users found.");
		} else {
			users.forEach(System.out::println);
		}
	}

	private static void borrowBook() {
		System.out.print("Enter user ID: ");
		Long userId = scanner.nextLong();
		System.out.print("Enter book ID: ");
		Long bookId = scanner.nextLong();
		scanner.nextLine();

		User user = userDAO.findById(userId);
		Book book = bookDAO.findById(bookId);
		if (user == null || book == null) {
			System.out.println("User or book not found!");
			return;
		}

		if (book.getAvailableCopies() <= 0) {
			System.out.println("No copies available!");
			return;
		}

		Borrow borrow = new Borrow(userId, bookId, LocalDate.now(), LocalDate.now().plusDays(14), "Borrowed");
		borrowDAO.save(borrow);
		book.setAvailableCopies(book.getAvailableCopies() - 1);
		bookDAO.update(book);
		System.out.println("Book borrowed successfully!");
	}

	private static void returnBook() {
		System.out.print("Enter borrow ID: ");
		Long id = scanner.nextLong();
		scanner.nextLine();
		Borrow borrow = borrowDAO.findById(id);
		if (borrow == null) {
			System.out.println("Borrow record not found!");
			return;
		}

		borrow.setStatus("Returned");
		borrowDAO.update(borrow);
		Book book = bookDAO.findById(borrow.getBookId());
		book.setAvailableCopies(book.getAvailableCopies() + 1);
		bookDAO.update(book);
		System.out.println("Book returned successfully!");
	}

	private static void viewBorrowings() {
		List<Borrow> borrowings = borrowDAO.findAll();
		if (borrowings.isEmpty()) {
			System.out.println("No borrowings found.");
		} else {
			borrowings.forEach(System.out::println);
		}
	}
}