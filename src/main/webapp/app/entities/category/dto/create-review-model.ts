import {CardDetails} from "../card-details";

/**
 * This class is used to create a comment about a card.
 * */
export class CreateReviewModel {
    cardDetailsId : number = 0;
    message : string = "";

    constructor(message:string,cardId:number) {
        this.message = message;
        this.cardDetailsId = cardId;
    }

    public static createEmptyCreateReview():CreateReviewModel {
        return new CreateReviewModel("",0);
    }
}
