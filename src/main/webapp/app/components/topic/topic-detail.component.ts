import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Topic} from "../../entities/topic/topic";
import {TopicService} from "../../services/topic.service";
import {CardDetailsService} from "../../services/card-details.service";
import {CardDetails} from "../../entities/category/card-details";
import {error} from "util";
import {CardDetailComponent} from "../cards/card-detail.component";

@Component({
    selector: 'topic-detail',
    templateUrl: 'html/topic-detail.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class TopicDetailComponent implements OnInit {
    private topic:Topic = Topic.createEmptyTopic();
    private cards:CardDetails[] = [];
    private currentCard:CardDetails;
    private categoryCards:CardDetails[] = [];
    private categoryCardsToAdd:CardDetails[] = [];


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _topicService:TopicService,
                private _cardDetailsService:CardDetailsService) {
    }


    ngOnInit():any {
        var topicId:number = +this._routeArgs.params["topicId"];

        this._topicService.getTopic(topicId).subscribe(
            data => {
                this.topic = this.topic.deserialize(data.json());
            }
        );

        this._cardDetailsService.getCardDetailsOfTopic(topicId).subscribe(
            data => {
                for (let card of data.json())
                    this.cards.push(CardDetails.createEmptyCard().deserialize(card));
            }
        );
    }

    public openAddCardModal():void {
        this.categoryCards = [];

        console.log(this.topic.categoryId.toString());
        this._cardDetailsService.getCardDetailsOfCategory(this.topic.categoryId).subscribe(
            data => {
                for (let card of data.json())
                    this.categoryCards.push(CardDetails.createEmptyCard().deserialize(card));
            });
    }

    public onCategoryCardClick(card:CardDetails):void {
        this.categoryCardsToAdd.push(card);
    }

    public onAddCardsClick() {
        for (let card of this.categoryCardsToAdd) {
            this._cardDetailsService.addCardToTopic(this.topic.topicId, card.cardDetailsId).subscribe(
                data => {
                    this.cards = [];

                    for (let card of data.json())
                        this.cards.push(CardDetails.createEmptyCard().deserialize(card));
                },
                error => console.log(error.json())
            );
            console.log(card.text);
        }
    }

    public onCardClick(card:CardDetails):void {
        this.currentCard = card;
    }

    public toAddNewSession(categoryId:number,topicId:number) {
        this._router.navigate(["/CreateSession", {categoryId: categoryId, topicId: topicId}])
    }
}