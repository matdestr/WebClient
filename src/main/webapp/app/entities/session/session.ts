import {Serializable} from "../../util/serializable";
import {User} from "../user/user";

export class Session implements Serializable<Session>{
    sessionId: number;
    categoryId: number;
    topicId: number;
    minNumberOfCardsPerParticipant:number;
    maxNumberOfCardsPerParticipant:number;
    participantsCanAddCards:boolean;
    currentParticipantPlaying : User;
    cardCommentsAllowed:boolean;
    amountOfCircles:number;
    type: string;


    constructor(minNumberOfCardsPerParticipant:number,maxNumberOfCardsPerParticipant:number,participantsCanAddCards:boolean,cardCommentsAllowed:boolean,amountOfCircles:number, categoryId:number, topicId:number, type:string, sessionId:number) {
        this.minNumberOfCardsPerParticipant = minNumberOfCardsPerParticipant;
        this.maxNumberOfCardsPerParticipant = maxNumberOfCardsPerParticipant;
        this.participantsCanAddCards = participantsCanAddCards;
        this.cardCommentsAllowed = cardCommentsAllowed;
        this.amountOfCircles = amountOfCircles;
        this.categoryId = categoryId;
        this.topicId = topicId;
        this.type =  type;
        this.sessionId = sessionId;
    }

    public static createEmptySession():Session {
        return new Session(0,0,false,false,0,0,0,"",0);
    }

    deserialize(object:Session):Session {
        this.categoryId = object.categoryId
        this.topicId = object.topicId;
        this.minNumberOfCardsPerParticipant = object.minNumberOfCardsPerParticipant;
        this.maxNumberOfCardsPerParticipant = object.maxNumberOfCardsPerParticipant;
        this.amountOfCircles = object.amountOfCircles;
        this.type = object.type;
        this.participantsCanAddCards = object.participantsCanAddCards;
        this.cardCommentsAllowed = object.cardCommentsAllowed;
        this.currentParticipantPlaying = object.currentParticipantPlaying;
        this.sessionId = object.sessionId;

        return this;
    }
}
