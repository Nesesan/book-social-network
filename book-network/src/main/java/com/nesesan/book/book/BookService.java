package com.nesesan.book.book;

import com.nesesan.book.common.PageResponse;
import com.nesesan.book.exception.OperationNotPermittedException;
import com.nesesan.book.file.FileStorageService;
import com.nesesan.book.history.BookTransactionHistory;
import com.nesesan.book.history.BookTransactionHistoryRepository;
import com.nesesan.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.nesesan.book.book.BookSpecification.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    private final FileStorageService fileStorageService;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }


    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow your own book");
        }
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        bookTransactionHistory.setReturnApproved(false);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approvedReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("The book is not returned yet. So you cannot approve this return."));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with the ID::" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
