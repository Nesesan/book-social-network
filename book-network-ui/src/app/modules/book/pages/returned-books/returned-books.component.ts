import { Component, OnInit } from '@angular/core';
import { PageResponseBorrowedBookResponse } from "../../../../services/models/page-response-borrowed-book-response";
import { BorrowedBookResponse } from "../../../../services/models/borrowed-book-response";
import { BookService } from "../../../../services/services/book.service";

@Component({
  selector: 'app-returned-books',
  templateUrl: './returned-books.component.html',
  styleUrls: ['./returned-books.component.scss']
})
export class ReturnedBooksComponent implements OnInit {

  returnedBooks: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 5;
  message = '';
  level = 'success';

  constructor(private bookService: BookService) { }

  ngOnInit(): void {
    this.findAllReturnedBooks();
  }

  private findAllReturnedBooks() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.returnedBooks = resp;
      },
      error: (err) => {
        this.level = 'error';
        this.message = 'Error loading returned books';
      }
    });
  }

  gotToFirstPage() {
    this.page = 0;
    this.findAllReturnedBooks();
  }

  goToPreviousPage() {
    if (this.page > 0) {
      this.page--;
      this.findAllReturnedBooks();
    }
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllReturnedBooks();
  }

  goToNextPage() {
    if (!this.isLastPage) {
      this.page++;
      this.findAllReturnedBooks();
    }
  }

  goToLastPage() {
    this.page = this.returnedBooks.totalPages as number - 1;
    this.findAllReturnedBooks();
  }

  get isLastPage(): boolean {
    return this.page === (this.returnedBooks.totalPages as number - 1);
  }

  approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned) {
      return;
    }
    this.bookService.approvedReturnBorrowBook({
      'book_id': book.id as number
    }).subscribe({
      next: () => {
        this.level = 'success';
        this.message = 'Book return approved';
        this.findAllReturnedBooks();
      },
      error: () => {
        this.level = 'error';
        this.message = 'The book is not returned yet';
      }
    });
  }
}
