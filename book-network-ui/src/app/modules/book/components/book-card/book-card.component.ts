import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {
  private _book:BookResponse = {};
  private _manage = false;
  private _bookCover: string | undefined;

  get book(): BookResponse {
    return this._book;
  }

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }

  @Input()
  get manage(): boolean {
    return this._manage;
  }

  set manage(value: boolean) {
    this._manage = value;
  }


  get bookCover(): string | undefined {
    if (this._book.cover) {
      return 'data:image/jpg;base64,' + this._book.cover;
    }
    return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFtIiwbQLKvRRQn_06612_CSC84SsKQTbvoQ&s";
  }

@Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
@Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
@Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
@Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
@Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
@Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  onShowDetails() {
    this.details.emit(this.book)
  }

  onBorrow() {
    this.borrow.emit(this.book)
  }

  onAddToWaitingList() {
    this.addToWaitingList.emit(this.book)
  }

  onEdit() {
    this.edit.emit(this.book)
  }

  onShare() {
    this.share.emit(this.book)
  }

  onArchive() {
    this.archive.emit(this.book)
  }
}
