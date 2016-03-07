import {Component, Input, Output, OnInit, EventEmitter} from 'angular2/core'
import {CardDetails} from "../../entities/category/card-details";

@Component({
    selector: 'card-detail',
    template: `
                <div [className]="cardClassName" (click)="onClick()">
                    <img class="card-img" [src]="card.imageUrl" alt="Card image">
                    <div class="card-block">
                        <p class="card-text">{{card.text}}</p>
                    </div>
                </div>
        `
})
export class CardDetailComponent implements OnInit {
    @Input() public card:CardDetails;
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

            if (this.active)
                this.cardClassName += " card-active";
            else
                this.cardClassName = this.cardClassName.replace("card-active", "");

            this.card.active = this.active;
        }

        this.cardClick.emit(this.card);

    }


}