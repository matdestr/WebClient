import {CardDetails} from "../category/card-details";
import {Serializable} from "../../util/serializable";

export class CardPosition implements Serializable<CardPosition> {
    /*public cardDetailsId : number;
    public text : string;
    public imageUrl : string;*/
    
    public cardDetails : CardDetails;
    public priority : number;
    
    public cardWidth : number;
    public cardHeight : number;
    public positionLeft : number;
    public positionTop : number;
    
    // TODO : All-args constructor obsolete ??
    constructor(cardDetails : CardDetails, priority : number,
                cardWidth : number, cardHeight : number, positionLeft : number, positionTop : number) {
        /*this.cardDetailsId = cardDetailsId;
        this.text = text;
        this.imageUrl = imageUrl;*/
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.positionLeft = positionLeft;
        this.positionTop = positionTop;
    }
    
    public static createEmptyCardPosition() : CardPosition {
        return new CardPosition(null, null, null, null, null, null);
    }


    deserialize(object : CardPosition) : CardPosition {
        this.cardDetails = CardDetails.createEmptyCard().deserialize(object.cardDetails);
        this.priority = object.priority;
        
        return this;
    }
}
