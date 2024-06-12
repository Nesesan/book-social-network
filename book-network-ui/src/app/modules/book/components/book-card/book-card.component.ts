import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {
  private _book: BookResponse = {};
  private _bookCover: string | undefined;
  private _manage:boolean = false;


  get book(): BookResponse {
    return this._book;
  }

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }


  get manage(): boolean {
    return this._manage;
  }
  @Input()
  set manage(value: boolean) {
    this._manage = value;
  }


  get bookCover(): string | undefined {
    if (this._book.cover){
    return  'data:image/jpg;base64, ' + this._book.cover;
    }
    return 'https://img-cdn.pixlr.com/image-generator/history/65bb506dcb310754719cf81f/ede935de-1138-4f66-8ed7-44bd16efc709/medium.webp';
  }

  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>()

  onBorrow() {
    this.borrow.emit(this._book);
  }

  onShowDetails() {
    this.details.emit(this._book);
}

  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book);
  }

  onEdit() {
    this.edit.emit(this._book);
  }

  onShare() {
    this.share.emit(this._book);
  }

  onArchive() {
    this.archive.emit(this._book);
  }
}
