import {Component, OnInit} from "angular2/core";
import {Router, RouteParams} from "angular2/router";

import {Topic} from "../../entities/topic/topic";
import {Category} from "../../entities/category/category";
import {CardDetails} from "../../entities/category/card-details";
import {User} from "../../entities/user/user";

import {TopicService} from "../../services/topic.service";
import {CategoryService} from "../../services/category.service";
import {CardDetailsService} from "../../services/card-details.service";

import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";

import {CardSearchPipe} from "../../util/pipes/search-pipe";
import {SearchBoxComponent} from "../widget/search-box";


@Component({
    selector: 'topic-card-chooser',
    templateUrl: "html/topic/topic-card-chooser.html",
    directives: [ToolbarComponent, CardDetailComponent, SearchBoxComponent],
    pipes: [CardSearchPipe]
})
export class TopicCardChooserComponent implements OnInit {
    private topic:Topic = Topic.createEmptyTopic();
    private categoryCards:CardDetails[] = [];
    private categoryCardsToAdd:CardDetails[] = [];
    private searchTerm:string = "";

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

                this._cardDetailsService.getCardDetailsOfCategory(this.topic.categoryId).subscribe(
                    data => {
                        for (let card of data.json())
                            this.categoryCards.push(CardDetails.createEmptyCard().deserialize(card));
                    });
            }
        );


    }

    public onCategoryCardClick(card:CardDetails):void {
        var index = this.categoryCardsToAdd.indexOf(card);
        console.log(index);
        console.log(JSON.stringify(card));

        if (index < 0) {
            this.categoryCardsToAdd.push(card);

        } else {
            if (!card.active)
                this.categoryCardsToAdd.splice(index, 1);
        }
    }

    public onAddCardsClick() {
        for (let catCard of this.categoryCardsToAdd)
            this._cardDetailsService.addCardToTopic(this.topic.topicId, catCard.cardDetailsId).subscribe();

        this.navigateUp();
    }

    public navigateUp() {
        this._router.navigate(["/TopicDetail", {topicId: this.topic.topicId}])
    }
}