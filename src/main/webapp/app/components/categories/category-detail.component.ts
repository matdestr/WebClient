import {Component, OnInit, Input} from "angular2/core";
import {ToolbarComponent} from "../widget/toolbar.component";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category";
import {CategoryService} from "../../services/category.service";
import {Topic} from "../../entities/topic/topic";
import {TopicService} from "../../services/topic.service";
import {Tag} from "../../entities/tag";
import {TagService} from "../../services/tag.service";
import {CardDetailsService} from "../../services/card-details.service";
import {CardDetails} from "../../entities/category/card-details";
import {error} from "util";
import {CardDetailComponent} from "../cards/card-detail.component";

@Component({
    selector: 'category-detail',
    templateUrl: 'html/category-detail.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class CategoryDetailComponent implements OnInit {
    private category:Category = Category.createEmptyCategory();
    private cards:CardDetails[] = [];
    private topics:Topic[] = [];
    private tags:Tag[] = [];
    private currentCard:CardDetails = CardDetails.createEmptyCard();


    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService:CategoryService,
                private _topicService:TopicService,
                private _cardDetailsService:CardDetailsService,
                private _tagService:TagService) {
    }


    ngOnInit():any {
        var categoryId:number = +this._routeArgs.params["categoryId"];

        this._categoryService.getCategory(categoryId).subscribe(
            data => this.category = data.json()
        );

        this._cardDetailsService.getCardDetailsOfCategory(categoryId).subscribe(
            data =>{
                for (let card of data.json())
                    this.cards.push(CardDetails.createEmptyCard().deserialize(card));
            },
            error => console.log(error.json)
        );

        this._topicService.getTopicsFromCategory(categoryId).subscribe(
            data => {
                for (let topic of data.json())
                    this.topics.push(Topic.createEmptyTopic().deserialize(topic));
            },
            error => console.log(error),
            () => console.log("Topics fetched")
        );

        this._tagService.getTags().subscribe(
            data => {
                for (let tag of data.json())
                    this.tags.push(Tag.createEmptyTag().deserialize(tag));
            }
        );
    }

    public toAddNewTopic(categoryId:number) {
        this._router.navigate(["/CreateTopic", {categoryId: categoryId}])
    }

    public toTopic(topicId:number) {
        this._router.navigate(["/TopicDetail", {topicId: topicId}])
    }

    public toAddNewCard(categoryId:number) {
        this._router.navigate(["/CreateCard", {categoryId: categoryId}])
    }

    public onCardClick(card:CardDetails):void {
        this.currentCard = card;
        console.log(card.text);
    }

}