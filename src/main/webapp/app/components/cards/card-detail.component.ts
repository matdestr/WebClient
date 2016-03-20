import {Component, Input, Output, OnInit, EventEmitter} from 'angular2/core'
import {NgIf} from 'angular2/common'
import {CardDetails} from "../../entities/category/card-details";

/**
 * Component for displaying and selecting a card.
 * */
@Component({
    selector: 'card-detail',
    template: `
                <div [className]="cardClassName" [class.card-active]="active" (click)="onClick()">
                    <img *ngIf="card.imageUrl != null"  class="card-img" [src]="card.imageUrl" alt="Card image">
                    <div class="card-block">
                        <p class="card-text">{{card.text}}</p>
                    </div>
                </div>
        `
})
export class CardDetailComponent implements OnInit {
    @Input() public card:CardDetails = CardDetails.createEmptyCard();
    @Input() public cardClassName:string;
    @Input() public clickable:boolean;
    @Input() public selectable:boolean;
    @Output() public cardClick:EventEmitter<CardDetails> = new EventEmitter();
    private active:boolean = false;


    ngOnInit():any {
        if (this.clickable)
            this.cardClassName += " card-clickable";
    }

    public onClick():void {
        if (this.selectable) {
            this.active = !this.active;
            this.card.active = this.active;
        }

        this.cardClick.emit(this.card);

    }


}