import { NgModule } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

import { BookRoutingModule } from './book-routing.module';
import { MenuComponent } from './components/menu/menu.component';
import { BookCardComponent } from './components/book-card/book-card.component';
import { RatingComponent } from './components/rating/rating.component';



@NgModule({
    declarations: [
        MenuComponent,
        BookCardComponent,
        RatingComponent,
    ],
    exports: [
        MenuComponent,
        BookCardComponent,
    ],
    imports: [
        CommonModule,
        BookRoutingModule,
        NgOptimizedImage
    ]
})
export class BookModule { }
