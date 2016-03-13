import {Component, OnInit, Input} from "angular2/core";
import {ChatMessage} from "../../entities/session/create-chat-message";
import {User} from "../../entities/user/user";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";
import {CardDetailComponent} from "../cards/card-detail.component";
import {ElementRef} from "angular2/core";
import {ViewChild} from "angular2/core";
import {CircleComponent} from "./circle.component";
import {CardPosition} from "../../entities/session/card-position";
import {SessionGameService} from "../../services/session-game.service";
import {SessionService} from "../../services/session.service";
import {Session} from "../../entities/session/session";
import {Circle} from "../../entities/session/circle";
import {NgFor} from "angular2/common";
import {CardDetails} from "../../entities/category/card-details";

@Component({
    selector: 'active-session',
    templateUrl: 'html/active-session.html',
    directives: [ToolbarComponent, CardDetailComponent, CircleComponent, NgFor]
})
export class ActiveSessionComponent implements OnInit {
    private static CIRCLE_RADIUS_STEP_DISTANCE : number = 100;
    private static CIRCLE_THICKNESS : number = 40;
    private static CARD_WIDTH : number = 60;
    private static CARD_HEIGHT : number = 80;
    
    private cardBorderThickness : number = 5;
    
    @ViewChild("chatbox")
    private chatContainer:ElementRef;
    
    private currentMessage:string = "";
    private messages:ChatMessage[] = [];
    private stompClient:any = null;
    
    private sessionId : number;
    private session : Session = Session.createEmptySession();
    private circles : Circle[];
    private cardPositions : CardPosition[];
    private selectedCard : CardPosition;
    
    private circleSpaceHeight : number = 700;
    private circleSpaceWidth : number = 700;
    
    private viewBoxWidth : number = 
        this.circleSpaceWidth + ((ActiveSessionComponent.CARD_WIDTH - ActiveSessionComponent.CIRCLE_THICKNESS) / 2) 
        + this.cardBorderThickness;
    
    private viewBoxHeight : number = 
        this.circleSpaceHeight + ((ActiveSessionComponent.CARD_HEIGHT - ActiveSessionComponent.CIRCLE_THICKNESS) / 2) 
        + this.cardBorderThickness;

    constructor(private _router:Router,
                private _routeArgs:RouteParams,
                private _sessionService : SessionService,
                private _gameService : SessionGameService) {
        
        this.sessionId = parseInt(_routeArgs.get('sessionId'));
        this.initSockets();
    }
    
    ngOnInit() : any {
        this._sessionService.getSession(this.sessionId)
            .subscribe(data => {
                this.session = this.session.deserialize(data.json());
                this.initCircles();
            }, error => console.error(error));
    }
    
    private initSockets() : void {
        var self:any = this;
        var socket = new SockJS('/kandoe/ws?token=' + localStorage.getItem("token"));
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, function(){
            self.stompClient.subscribe('/topic/session/messages', function (data) {
                var message:ChatMessage = JSON.parse(data.body);
                self.messages.push(message);
                self.chatContainer.nativeElement.scrollTop = self.chatContainer.nativeElement.scrollHeight;
            });

            self.stompClient.subscribe('/topic/sessions/' + self.sessionId + '/positions', function (data) {
                console.log('Received web socket message for card position update');
                self.updateCardPositions();
            })
        });
    }
    
    private initCircles() : void {
        this.circles = [];
        var maxRadius : number = this.session.amountOfCircles * ActiveSessionComponent.CIRCLE_RADIUS_STEP_DISTANCE;

        for (let i = 0; i < this.session.amountOfCircles; i++) {
            let circle : Circle = new Circle(i + 1);

            circle.radius = maxRadius - (ActiveSessionComponent.CIRCLE_THICKNESS / 2)
                - ActiveSessionComponent.CIRCLE_RADIUS_STEP_DISTANCE * (circle.priority - 1);
            
            this.circles.push(circle);
        }

        this.circleSpaceHeight = (this.circles.length * ActiveSessionComponent.CIRCLE_RADIUS_STEP_DISTANCE) * 2;
        this.circleSpaceWidth = (this.circles.length * ActiveSessionComponent.CIRCLE_RADIUS_STEP_DISTANCE) * 2;

        this.viewBoxWidth = 
            this.circleSpaceWidth + (ActiveSessionComponent.CARD_WIDTH - ActiveSessionComponent.CIRCLE_THICKNESS)
            + this.cardBorderThickness;
        
        this.viewBoxHeight = 
            this.circleSpaceHeight + (ActiveSessionComponent.CARD_HEIGHT - ActiveSessionComponent.CIRCLE_THICKNESS)
            + this.cardBorderThickness;
        
        document.getElementById('game-svg').setAttribute('viewBox', '0 0 ' + this.viewBoxWidth + ' ' + this.viewBoxHeight);
        
        this.refreshCardPositions();
    }
    
    private refreshCardPositions() : void {
        this.cardPositions = [];
        
        this._gameService.getCardPositions(this.sessionId)
            .subscribe(data => {
                for (let cardPosition of data.json()) {
                    this.cardPositions.push(CardPosition.createEmptyCardPosition().deserialize(cardPosition));
                }
                
                this.selectedCard = this.cardPositions[0];
                this.spreadCardsOnCircles();
            }, error => console.error(error));
    }
    
    private spreadCardsOnCircles() : void {
        for (var circle of this.circles) {
            circle.cardPositions = this.cardPositions.filter(
                function (cardPosition) : boolean {
                    return cardPosition.priority == circle.priority;
                }
            );
            
            let angle = 0;
            let step = (2 * Math.PI) / circle.cardPositions.length;
            
            let containerWidth : number = this.viewBoxWidth;
            let containerHeight : number = this.viewBoxHeight;
            
            let centerWidth : number = containerWidth / 2;
            let centerHeight : number = containerHeight / 2;
            
            for (let cardPosition of circle.cardPositions) {
                cardPosition.positionLeft = Math.round(
                    centerWidth + (circle.radius * Math.cos(angle)) - (ActiveSessionComponent.CARD_WIDTH / 2)
                );
                
                cardPosition.positionTop = Math.round(
                    centerHeight + (circle.radius * Math.sin(angle)) - (ActiveSessionComponent.CARD_HEIGHT / 2)
                );
                
                cardPosition.cardWidth = ActiveSessionComponent.CARD_WIDTH;
                cardPosition.cardHeight = ActiveSessionComponent.CARD_HEIGHT;
                
                angle += step;
            }
        }
    }
    
    private onCardClicked(cardPosition : CardPosition) : void {
        this.selectedCard = cardPosition;
    }
    
    private previousCard() : void {
        let index : number = this.cardPositions.indexOf(this.selectedCard);
        let previousIndex = this.cardPositions.length - 1;

        if (index > 0)
            previousIndex = index - 1;

        this.selectedCard = this.cardPositions[previousIndex];
        
        console.log('previous index: ' + previousIndex);
    }
    
    private nextCard() : void {
        let index : number = this.cardPositions.indexOf(this.selectedCard);
        let nextIndex = 0;

        if (index < this.cardPositions.length - 1)
            nextIndex = index + 1;

        this.selectedCard = this.cardPositions[nextIndex];
    }
    
    private upvoteCurrentCard() : void {
        /*this._gameService.updateCardPosition(this.sessionId, this.selectedCard)
            .subscribe(data => {
                let updatedCardPosition : CardPosition = CardPosition.createEmptyCardPosition().deserialize(data.json());
                this.updateCardPosition(updatedCardPosition);
                this.spreadCardsOnCircles();
            }, error => console.error(error));*/
        
        console.log('Updating card position ...');
        
        this._gameService.updateCardPosition(this.sessionId, this.selectedCard)
            .subscribe(data => {
                console.log('Card position successfully updated');
            }, error => console.log('Could not update card position'));
    }

    // TODO : Move to service?
    /**
     * This method updates an existing card position instead of replacing it to preserve CSS animations.
     * */
    private updateCardPosition(cardPosition : CardPosition) : void {
        let cardPositionToUpdate : CardPosition = null;
        
        for (let existingCardPosition of this.cardPositions) {
            if (existingCardPosition.cardDetails.cardDetailsId == cardPosition.cardDetails.cardDetailsId)
                cardPositionToUpdate = existingCardPosition;
        }
        
        cardPositionToUpdate.priority = cardPosition.priority;
    }
    
    /**
     * This method updates the existing card positions instead of replacing the array to preserve CSS animations.
     * */
    private updateCardPositions() : void {
        this._gameService.getCardPositions(this.sessionId)
            .subscribe(data => {
                for (let cardPosition of data.json()) {
                    let updatedCardPosition : CardPosition = CardPosition.createEmptyCardPosition().deserialize(cardPosition);
                    this.updateCardPosition(updatedCardPosition);
                }

                this.spreadCardsOnCircles();
            }, error => console.error(error));
    }

    public send() : void {
        if (this.stompClient)
            if (this.currentMessage || this.currentMessage.length != 0) {
                console.log(this.currentMessage.length);
                this.stompClient.send("/messages", null, JSON.stringify({'message': this.currentMessage}));
                this.currentMessage = "";
            }
    }
}
