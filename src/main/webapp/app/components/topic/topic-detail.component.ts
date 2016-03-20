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

import {error} from "util";
import {Session} from "../../entities/session/session";

/**
 * This component is responsible for all the functionality of the topic detail page
 */
@Component({
    selector: 'topic-detail',
    templateUrl: 'html/topic/topic-detail.html',
    directives: [ToolbarComponent, CardDetailComponent]
})
export class TopicDetailComponent implements OnInit {
    private topic:Topic = Topic.createEmptyTopic();
    private cards:CardDetails[] = [];
    private sessions:Session[] = [];
    private sessionSubset:Session[]=[];
    private currentCard:CardDetails = CardDetails.createEmptyCard();
    private counterSesBegin:number=0;
    private counterSesEnd:number=4;
    private myLeftSesDisplay:string="block";
    private myRightSesDisplay:string="block";

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

        this._topicService.getSessionsFromTopic(topicId).subscribe(
            data => {
                for (let session of data.json()){
                    this.sessions.push(Session.createEmptySession().deserialize(session));
                }
                this.sessionSubset = this.sessions.slice(0,4);
                if(this.sessions.length<=4) {
                    this.myLeftSesDisplay = "none";
                    this.myRightSesDisplay = "none";
                }else {
                    this.myLeftSesDisplay = "none";
                    this.myRightSesDisplay = "block";
                }
            },
            error => console.log(error),
            () => console.log("Sessions fetched")
        );
    }

    public openCardChooser():void {
        this._router.navigate(["/TopicCardChooser", {topicId: this.topic.topicId}]);
    }

    public onCardClick(card:CardDetails):void {
        this.currentCard = card;
    }

    public toAddNewSession(categoryId:number, topicId:number) {
        this._router.navigate(["/CreateSession", {categoryId: categoryId, topicId: topicId}])
    }

    public toSession(sessionId:number) {
        this._router.navigate(["/InviteUsers", {sessionId: sessionId}])
    }

    public nextSesPage(){
        this.myLeftSesDisplay = "block";
        if(this.counterSesEnd >= this.sessions.length-1){
            this.myRightSesDisplay="none";
        }
        if(this.counterSesEnd >= this.sessions.length){
            return;
        }
        else{
            this.counterSesBegin++;
            this.counterSesEnd++;
            this.sessionSubset= this.sessions.slice(this.counterSesBegin,this.counterSesEnd);
        }
    }

    public previousSesPage(){
        this.myRightSesDisplay = "block";
        if(this.counterSesBegin <= 1){
            this.myLeftSesDisplay="none";
        }
        if(this.counterSesBegin <= 0){
            return;
        }  else {
            this.counterSesBegin--;
            this.counterSesEnd--;
            this.sessionSubset = this.sessions.slice(this.counterSesBegin, this.counterSesEnd);
        }
    }

    public onEditClick(topicName:string){
        this._topicService.setTopicName(this.topic.topicId,topicName).subscribe(
            () => console.log("Succeeeeeed"),
            error => console.log(error));
    }
}