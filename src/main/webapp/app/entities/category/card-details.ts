import {Serializable} from "../../util/serializable";
import {User} from "../user/user";
import {Category} from "./category";
import {Topic} from "../topic/topic";
import {Comment} from "./comment";

export class CardDetails implements Serializable<CardDetails> {

    public cardDetailsId:number;
    public text:string;
    public imageUrl:string;
    public active:boolean;
    public comments:Comment[];

    constructor(cardDetailsId:number, text:string, imageUrl:string, active:boolean) {
        this.cardDetailsId = cardDetailsId;
        this.text = text;
        this.imageUrl = imageUrl;
        this.active = active;
        this.comments= [];
    }

    public static createEmptyCard():CardDetails {
        return new CardDetails(0, "", "", false);
    }

    deserialize(object:CardDetails):CardDetails {
        this.cardDetailsId = object.cardDetailsId;
        this.text = object.text;
        this.imageUrl = object.imageUrl;
        this.active = object.active;

        return this;
    }
}