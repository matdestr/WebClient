import {Component, Input, EventEmitter} from 'angular2/core'
import {CardDetails} from "../../entities/category/card-details";

@Component({
    selector: 'card-detail',
    template: `
                <div [className]="cardClassName">
                    <img class="card-img" [src]="card.imageUrl" alt="Card image">
                    <div class="card-block">
                        <p class="card-text">{{card.text}}</p>
                    </div>
                </div>
        `
})
export class CardDetailComponent {
    @Input() public card:CardDetails;
    @Input() public cardClassName;
}