import {CardDetails} from "../card-details";
export class CreateReviewModel {
    cardId:number=0;
    message:string="";

    constructor(message:string,cardId:number) {
        this.message=message;
        this.cardId=cardId;
    }

    public static createEmptyCreateReview():CreateReviewModel {
        return new CreateReviewModel("",0);
    }
}
