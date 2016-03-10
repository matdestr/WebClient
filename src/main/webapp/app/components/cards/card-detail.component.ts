import {Component, Input, Output, OnInit, EventEmitter} from 'angular2/core'
import {NgIf} from 'angular2/common'
import {CardDetails} from "../../entities/category/card-details";

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

        this.card.imageUrl = "img/ok.jpg";
        this.card.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

    public onClick():void {
        if (this.selectable) {
            this.active = !this.active;
            this.card.active = this.active;
        }

        this.cardClick.emit(this.card);

    }


}