import {Component, Input, OnInit, Inject, ViewChild, ElementRef} from "angular2/core";
import {getUsername} from "../../libraries/angular2-jwt";

import {Session} from "../../entities/session/session";
import {Circle} from "../../entities/session/circle";
import {CardPosition} from "../../entities/session/card-position";
import {User} from "../../entities/user/user";
import {ChatMessage} from "../../entities/session/create-chat-message";

import {SessionService} from "../../services/session.service";
import {SessionGameService} from "../../services/session-game.service";

/**
 * This component is responsible for the functionality of playing a circle game of a session.
 * This functionality includes things such as generating circles and positioning the cards on those circles.
 * */
@Component({
    selector: 'session-in-progress',
    templateUrl: 'html/session/session-in-progress.html'
})
export class SessionInProgressComponent implements OnInit {
    private static CIRCLE_RADIUS_STEP_DISTANCE : number = 100;
    private static CIRCLE_THICKNESS : number = 40;

    private circleSpaceHeight : number = 700;
    private circleSpaceWidth : number = 700;
    private viewBoxWidth : number;
    private viewBoxHeight : number;
    
    private cardWidth : number = 60;
    private cardHeight : number = 80;
    private cardBorderThickness : number = 5;
    
    @Input()
    private session : Session;
    private circles : Circle[];
    private cardPositions : CardPosition[];
    private selectedCard : CardPosition;
    private currentParticipant : User;

    @ViewChild("chatbox")
    private chatContainer : ElementRef;
    
    private currentMessage : string = '';
    private messages : ChatMessage[] = [];
    private stompClient : any;
    
    private username : string;
    private currentParticipantUsername : string;
    private userIsOrganizer : boolean;

    constructor(private _sessionService : SessionService,
                private _sessionGameService : SessionGameService,
                @Inject('App.TokenName') private tokenName : string) {}

    ngOnInit() : any {
        this.username = getUsername(localStorage.getItem(this.tokenName));
        
        if (this.username == this.session.organizer.username)
            this.userIsOrganizer = true;
        
        this.viewBoxWidth = this.circleSpaceWidth + ((this.cardWidth - SessionInProgressComponent.CIRCLE_THICKNESS) / 2) + this.cardBorderThickness;
        this.viewBoxHeight = this.circleSpaceHeight + ((this.cardHeight - SessionInProgressComponent.CIRCLE_THICKNESS) / 2) + this.cardBorderThickness;
        
        this.loadChat();
        this.initSockets();
        this.initCircles();

        if (this.session.currentParticipantPlaying == null) {
            this._sessionService.getSession(this.session.sessionId)
                .subscribe(data => {
                    this.session = Session.createEmptySession().deserialize(data.json());
                    this.currentParticipant = this.session.currentParticipantPlaying;
                    this.currentParticipantUsername = this.session.currentParticipantPlaying.username;
                }, error => {
                    console.error('Could not retrieve new session information');
                    console.error(error);
                });
        } else {
            this.currentParticipant = this.session.currentParticipantPlaying;
            this.currentParticipantUsername = this.session.currentParticipantPlaying.username;
        }
    }

    private loadChat() : void {
        this.messages = [];

        this._sessionGameService.getChatMessages(this.session.sessionId)
            .subscribe(
                data => {
                    for (let chatMessage of data.json()) {
                        this.messages.push(chatMessage);
                    }
                },
                error => {
                    console.log('Could not retrieve chat messages');
                    console.error(error);
                }
            );
    }
    
    private initSockets() : void {
        let self : any = this;
        let token = localStorage.getItem(this.tokenName);
        let socket = new SockJS('/kandoe/ws/sockjs?token=' + token);
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, function() {
            self.stompClient.subscribe('/topic/sessions/' + self.session.sessionId + '/messages', function(data) {
                let message : ChatMessage = JSON.parse(data.body);
                self.messages.push(message);
                self.chatContainer.nativeElement.scrollTop = self.chatContainer.nativeElement.scrollHeight;
            });
            
            self.stompClient.subscribe('/topic/sessions/' + self.session.sessionId + '/positions', function(data) {
                self.updateCardPositions();
            });
            
            self.stompClient.subscribe('/topic/sessions/' + self.session.sessionId + '/current-participant', function(data) {
                let currentParticipant : User = JSON.parse(data.body);
                self.updateCurrentParticipant(currentParticipant);
            });
        });
    }
    
    private initCircles() : void {
        this.circles = [];
        let maxRadius : number = this.session.amountOfCircles * SessionInProgressComponent.CIRCLE_RADIUS_STEP_DISTANCE;
        
        for (let i = 0; i < this.session.amountOfCircles; i++) {
            let circle : Circle = new Circle(i + 1);
            
            circle.radius = maxRadius - (SessionInProgressComponent.CIRCLE_THICKNESS / 2) 
                - SessionInProgressComponent.CIRCLE_RADIUS_STEP_DISTANCE * (circle.priority - 1);
            
            this.circles.push(circle);
        }
        
        this.circleSpaceHeight = (this.circles.length * SessionInProgressComponent.CIRCLE_RADIUS_STEP_DISTANCE) * 2;
        this.circleSpaceWidth = (this.circles.length * SessionInProgressComponent.CIRCLE_RADIUS_STEP_DISTANCE) * 2;
        
        this.viewBoxWidth = this.circleSpaceWidth + (this.cardWidth - SessionInProgressComponent.CIRCLE_THICKNESS) + this.cardBorderThickness;
        this.viewBoxHeight = this.circleSpaceHeight + (this.cardHeight - SessionInProgressComponent.CIRCLE_THICKNESS) + this.cardBorderThickness;

        document.getElementById('game-svg').setAttribute('viewBox', '0 0 ' + this.viewBoxWidth + ' ' + this.viewBoxHeight);
        this.refreshCardPositions();
    }

    private refreshCardPositions() : void {
        this.cardPositions = [];

        this._sessionGameService.getCardPositions(this.session.sessionId)
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
                    centerWidth + (circle.radius * Math.cos(angle)) - (this.cardWidth / 2)
                );

                cardPosition.positionTop = Math.round(
                    centerHeight + (circle.radius * Math.sin(angle)) - (this.cardHeight / 2)
                );

                cardPosition.cardWidth = this.cardWidth;
                cardPosition.cardHeight = this.cardHeight;

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
    }

    private nextCard() : void {
        let index : number = this.cardPositions.indexOf(this.selectedCard);
        let nextIndex = 0;

        if (index < this.cardPositions.length - 1)
            nextIndex = index + 1;

        this.selectedCard = this.cardPositions[nextIndex];
    }

    private upvoteCurrentCard() : void {
        console.log('Updating card position ...');

        this._sessionGameService.updateCardPosition(this.session.sessionId, this.selectedCard)
            .subscribe(data => {
                console.log('Card position successfully updated');
            }, error => console.log('Could not update card position'));
    }

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
        this._sessionGameService.getCardPositions(this.session.sessionId)
            .subscribe(data => {
                for (let cardPosition of data.json()) {
                    let updatedCardPosition : CardPosition = CardPosition.createEmptyCardPosition().deserialize(cardPosition);
                    this.updateCardPosition(updatedCardPosition);
                }

                this.spreadCardsOnCircles();
            }, error => console.error(error));
    }

    private updateCurrentParticipant(user : User) : void {
        this.currentParticipant = user;
        this.currentParticipantUsername = this.currentParticipant.username;
    }
    
    private endSession() {
        this._sessionGameService.endSession(this.session.sessionId)
            .subscribe(
                data => {
                    console.log('Ended session');
                },
                error => {
                    console.log('Could not end session');
                }
            )
    }

    /**
     * This method sends the currently filled in chat message
     * */
    public send() : void {
        if (this.stompClient) {
            if (this.currentMessage || this.currentMessage.length != 0) {
                console.log(this.currentMessage.length);
                this.stompClient.send('/sessions/' + this.session.sessionId + '/messages', null, JSON.stringify({'message': this.currentMessage}));
                this.currentMessage = "";
            }
        }
    }
}
