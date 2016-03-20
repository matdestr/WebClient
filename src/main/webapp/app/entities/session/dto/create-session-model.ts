import DateTimeFormat = Intl.DateTimeFormat;

/**
 * entity for the creation of a session
 */

export class CreateSessionModel {
    categoryId: number = 0;
    topicId: number = null;
    minNumberOfCardsPerParticipant:number = null;
    maxNumberOfCardsPerParticipant:number = null;
    participantsCanAddCards:boolean = false;
    cardCommentsAllowed:boolean = false;
    amountOfCircles:number = null;
    type:string = "";
    startDateTime:string = null;
    secondsBetweenMoves:number = null;


    constructor(minNumberOfCardsPerParticipant:number,maxNumberOfCardsPerParticipant:number,participantsCanAddCards:boolean,cardCommentsAllowed:boolean,amountOfCircles:number, categoryId:number, topicId:number, type:string,startDateTime:string,secondsBetweenMoves:number) {
        this.minNumberOfCardsPerParticipant = minNumberOfCardsPerParticipant;
        this.maxNumberOfCardsPerParticipant = maxNumberOfCardsPerParticipant;
        this.participantsCanAddCards = participantsCanAddCards;
        this.cardCommentsAllowed = cardCommentsAllowed;
        this.amountOfCircles = amountOfCircles;
        this.categoryId = categoryId;
        this.topicId = topicId;
        this.type = type;
        this.startDateTime = startDateTime;
        this.secondsBetweenMoves = secondsBetweenMoves;
    }

    public static createEmptyCreateSession():CreateSessionModel {
        return new CreateSessionModel(null,null,false,false,null,0,null,"","",null);
    }
}