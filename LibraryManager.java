import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class LibraryManager {
    private List<Book> books;
    private List<LibrarySubscriber> subscribers;

    public LibraryManager() {
        books = new ArrayList<>();
        subscribers = new ArrayList<>();
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<LibrarySubscriber> getSubscribers() {
        return subscribers;
    }

    public void loadBooksFromFile(String filename) {
        books = DataStorage.loadBooksFromFile(filename);
    }

    public void loadSubscribersFromFile(String filename) {
        subscribers = DataStorage.loadSubscribersFromFile(filename);
    }

    public void saveBooksToFile(String filename) {
        DataStorage.saveBooksToFile(books, filename);
    }

    public void saveSubscribersToFile(String filename) {
        DataStorage.saveSubscribersToFile(subscribers, filename);
    }

    public void clearFiles(String bookFilename, String subscriberFilename) {
        DataStorage.clearFiles(bookFilename, subscriberFilename);
    }

    public void addBook(String title, String author, int bookId) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                System.out.println("Error: Book with this title already exists.");
                return;
            }
        }
        books.add(new Book(title, author, bookId));
        System.out.println("Book added.");
    }

    public void addSubscriber(String firstName, String lastName) {
        try {
            checkDuplicateSubscriber(firstName, lastName);
            subscribers.add(new LibrarySubscriber(new Library("City Library"), firstName, lastName));
            System.out.println("Subscriber added successfully.");
        } catch (DuplicateNameException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkDuplicateSubscriber(String firstName, String lastName) throws DuplicateNameException {
        for (LibrarySubscriber subscriber : subscribers) {
            if (subscriber.getFirstName().equalsIgnoreCase(firstName) &&
                    subscriber.getLastName().equalsIgnoreCase(lastName)) {
                throw new DuplicateNameException("Subscriber with the name " + firstName + " " + lastName + " already exists.");
            }
        }
    }

    public void displayBooks() {
        System.out.println("Books in Library:");
        for (Book book : books) {
            System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Book ID: " + book.getBookId());
        }
    }

    public void displaySubscribers() {
        System.out.println("Library Subscribers:");
        for (LibrarySubscriber subscriber : subscribers) {
            System.out.println("Name: " + subscriber.getFullName());
        }
    }

    public void sortBooksByTitle() {
        Collections.sort(books);
        displayBooks();
    }

    public void sortSubscribersByName() {
        Collections.sort(subscribers);
        displaySubscribers();
    }

    public void groupSubscribersByBorrowedBooks() {
        subscribers.sort((s1, s2) -> Integer.compare(s2.getBorrowedBookCount(), s1.getBorrowedBookCount()));
        for (LibrarySubscriber subscriber : subscribers) {
            System.out.println("Name: " + subscriber.getFullName() + " - Borrowed Books: " + subscriber.getBorrowedBookCount());
        }
    }

    public void userBorrowsBook(Scanner scanner) {
        if (subscribers.isEmpty() || books.isEmpty()) {
            System.out.println("Error: No subscribers or books available.");
            return;
        }

        System.out.println("Select a subscriber:");
        for (int i = 0; i < subscribers.size(); i++) {
            System.out.println((i + 1) + ". " + subscribers.get(i).getFullName());
        }

        int subscriberIndex;
        while (true) {
            try {
                System.out.print("Enter subscriber number: ");
                subscriberIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                if (subscriberIndex < 0 || subscriberIndex >= subscribers.size()) {
                    System.out.println("Invalid subscriber number. Try again.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        System.out.println("Select a book to borrow:");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i).getTitle() + " by " + books.get(i).getAuthor());
        }

        int bookIndex;
        while (true) {
            try {
                System.out.print("Enter book number: ");
                bookIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                if (bookIndex < 0 || bookIndex >= books.size()) {
                    System.out.println("Invalid book number. Try again.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        LibrarySubscriber selectedSubscriber = subscribers.get(subscriberIndex);
        Book selectedBook = books.get(bookIndex);

        try {
            if (selectedSubscriber.getBorrowedBooks().contains(selectedBook)) {
                throw new BookNotAvailableException("This book has already been borrowed by the subscriber.");
            }
            selectedSubscriber.addBook(selectedBook);
            System.out.println(selectedSubscriber.getFullName() + " has borrowed \"" + selectedBook.getTitle() + "\" by " + selectedBook.getAuthor());
        } catch (BookNotAvailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
