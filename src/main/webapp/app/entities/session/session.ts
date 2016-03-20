import {Serializable} from "../../util/serializable";

import {User} from "../user/user";
import {SessionStatus} from "./session-status";
import {ParticipantInfo} from "./participant-info";

/**
 * entity that represents an Session JSON object
 */

export class Session implements Serializable<Session>{
    sessionId : number;
    categoryId : number;
    topicId : number;
    organizer : User;
    participantInfo : ParticipantInfo[];
    currentParticipantPlaying : User;
    
    minNumberOfCardsPerParticipant : number;
    maxNumberOfCardsPerParticipant : number;
    participantsCanAddCards : boolean;
    cardCommentsAllowed : boolean;

    sessionStatus : SessionStatus;
    amountOfCircles : number;
    type : string;

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

    deserialize(object : Session):Session {
        this.participantInfo = [];
        
        this.sessionId = object.sessionId;
        this.categoryId = object.categoryId;
        this.topicId = object.topicId;
        this.organizer = User.createEmptyUser().deserialize(object.organizer);
        
        for (let p of object.participantInfo) {
            this.participantInfo.push(new ParticipantInfo().deserialize(p));
        }
        
        if (object.currentParticipantPlaying != null)
            this.currentParticipantPlaying = User.createEmptyUser().deserialize(object.currentParticipantPlaying);
        
        this.minNumberOfCardsPerParticipant = object.minNumberOfCardsPerParticipant;
        this.maxNumberOfCardsPerParticipant = object.maxNumberOfCardsPerParticipant;
        this.participantsCanAddCards = object.participantsCanAddCards;
        this.cardCommentsAllowed = object.cardCommentsAllowed;

        this.sessionStatus = object.sessionStatus;
        this.amountOfCircles = object.amountOfCircles;
        this.type = object.type;

        return this;
    }
}
