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
import {Session} from "../../entities/session/session";
import {SessionService} from "../../services/session.service";
import {SessionListItem} from "../../entities/session/session-list-item";

/**
 * This component is responsible for all the functionality of the category detail page.
 */
@Component({
    selector: 'category-detail',
    templateUrl: 'html/category-detail.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class CategoryDetailComponent implements OnInit {
    private category:Category = Category.createEmptyCategory();
    private cards:CardDetails[] = [];
    private topics:Topic[] = [];
    private topicSubSet:Topic[] = [];
    private sessions:SessionListItem[] = [];
    private sessionSubset:SessionListItem[] = [];
    private selectedTags:Tag[] = [];
    private tags:Tag[] = [];
    private listTagId:number[] = [];
    private currentCard:CardDetails = CardDetails.createEmptyCard();
    private categoryId:number;
    private counterTopBegin:number = 0;
    private counterTopEnd:number = 4;
    private myLeftTopDisplay:string = "block";
    private myRightTopDisplay:string = "block";
    private counterSesBegin:number = 0;
    private counterSesEnd:number = 4;
    private myLeftSesDisplay:string = "block";
    private myRightSesDisplay:string = "block";

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _categoryService:CategoryService,
                private _topicService:TopicService,
                private _cardDetailsService:CardDetailsService,
                private _tagService:TagService) {
    }


    ngOnInit():any {
        this.categoryId = +this._routeArgs.params["categoryId"];

        this._categoryService.getCategory(this.categoryId).subscribe(
            data => {
                this.category = data.json()
                for (let tag of this.category.tags) {
                    this.selectedTags.push(Tag.createEmptyTag().deserialize(tag));
                }
            }
        );

        this._cardDetailsService.getCardDetailsOfCategory(this.categoryId).subscribe(
            data => {
                for (let card of data.json())
                    this.cards.push(CardDetails.createEmptyCard().deserialize(card));
            },
            error => console.log(error.json)
        );

        this._topicService.getTopicsFromCategory(this.categoryId).subscribe(
            data => {
                if (data.json() == null) {
                    this.myLeftTopDisplay = "none";
                    this.myRightTopDisplay = "none";
                } else {
                    for (let topic of data.json()) {
                        this.topics.push(Topic.createEmptyTopic().deserialize(topic));
                    }
                    this.topicSubSet = this.topics.slice(0, 4);
                    if (this.topics.length <= 4) {
                        this.myLeftTopDisplay = "none";
                        this.myRightTopDisplay = "none";
                    } else {
                        this.myLeftTopDisplay = "none";
                        this.myRightTopDisplay = "block";
                    }
                }
            },
            error => console.log(error),
            () => console.log("Topics fetched")
        );

        this._categoryService.getSessionsFromCategory(this.categoryId).subscribe(
            data => {
                if (data.json() == null) {
                    this.myLeftSesDisplay = "none";
                    this.myRightSesDisplay = "none";
                } else {
                    for (let session of data.json()) {
                        this.sessions.push(SessionListItem.createEmptySessionListItem().deserialize(session));
                    }
                    this.sessionSubset = this.sessions.slice(0, 4);
                    if (this.sessions.length <= 4) {
                        this.myLeftSesDisplay = "none";
                        this.myRightSesDisplay = "none";
                    } else {
                        this.myLeftSesDisplay = "none";
                        this.myRightSesDisplay = "block";
                    }
                }
            },
            error => console.log(error),
            () => console.log("Sessions fetched")
        );

        this._tagService.getTags().subscribe(
            data => {
                for (let tag of data.json()) {
                    //TODO: Check if tag is already in the category
                    this.tags.push(Tag.createEmptyTag().deserialize(tag));
                }
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

    public toAddNewSession(categoryId:number) {
        this._router.navigate(["/CreateSession", {categoryId: categoryId}])
    }

    public toSession(sessionId:number) {
        //this._router.navigate(["/InviteUsers", {sessionId: sessionId}])
        this._router.navigate(['/Session', {sessionId: sessionId}]);
    }

    public nextTopPage() {
        this.myLeftTopDisplay = "block";
        if (this.counterTopEnd >= this.topics.length - 1) {
            this.myRightTopDisplay = "none";
        }
        if (this.counterTopEnd >= this.topics.length) {
            return;
        }
        else {
            this.counterTopBegin++;
            this.counterTopEnd++;
            this.topicSubSet = this.topics.slice(this.counterTopBegin, this.counterTopEnd);
        }
    }

    public previousTopPage() {
        this.myRightTopDisplay = "block";
        if (this.counterTopBegin <= 1) {
            this.myLeftTopDisplay = "none";
        }
        if (this.counterTopBegin <= 0) {
            return;
        } else {
            this.counterTopBegin--;
            this.counterTopEnd--;
            this.topicSubSet = this.topics.slice(this.counterTopBegin, this.counterTopEnd);
        }
    }

    public onTagClick(tag:Tag):void {
        this.listTagId.push(tag.tagId)
        console.log(this.listTagId);

    }

    public saveTags():void {
        console.log("saveTags");
        this._categoryService.addTags(this.listTagId, this.category.categoryId).subscribe(
            () => {
                console.log("succeed");
                this.selectedTags = [];
                this._categoryService.getCategory(this.categoryId).subscribe(
                    data => {
                        this.category = data.json();
                        for (let tag of this.category.tags) {
                            this.selectedTags.push(Tag.createEmptyTag().deserialize(tag));
                        }
                    }
                );
            },
            error => console.log(error)
        );
        this.listTagId = [];
    }

    public nextSesPage() {
        this.myLeftSesDisplay = "block";
        if (this.counterSesEnd >= this.sessions.length - 1) {
            this.myRightSesDisplay = "none";
        }
        if (this.counterSesEnd >= this.sessions.length) {
            return;
        }
        else {
            this.counterSesBegin++;
            this.counterSesEnd++;
            this.sessionSubset = this.sessions.slice(this.counterSesBegin, this.counterSesEnd);
        }
    }

    public previousSesPage() {
        this.myRightSesDisplay = "block";
        if (this.counterSesBegin <= 1) {
            this.myLeftSesDisplay = "none";
        }
        if (this.counterSesBegin <= 0) {
            return;
        } else {
            this.counterSesBegin--;
            this.counterSesEnd--;
            this.sessionSubset = this.sessions.slice(this.counterSesBegin, this.counterSesEnd);
        }
    }

    public onEditClick(categoryName:string) {
        this._categoryService.setCategoryName(this.category.categoryId, categoryName).subscribe(
            () => console.log("Succeeeeeed"),
            error => console.log(error));
    }

}