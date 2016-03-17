import {CardDetails} from "../card-details";
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
