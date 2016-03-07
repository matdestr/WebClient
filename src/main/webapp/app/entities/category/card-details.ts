import {Serializable} from "../../util/serializable";
import {User} from "../user/user";
import {Category} from "./category";
import {Topic} from "../topic/topic";

export class CardDetails implements Serializable<CardDetails> {

    public cardDetailsId:number;
    public text:string;
    public imageUrl:string;
    // todo when comments needed, add comments array here!


    constructor(cardDetailsId:number, text:string, imageUrl:string) {
        this.cardDetailsId = cardDetailsId;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public static createEmptyCard():CardDetails {
        return new CardDetails(0, "", "");
    }

    deserialize(object:CardDetails):CardDetails {
        this.cardDetailsId = object.cardDetailsId;
        this.text = object.text;
        this.imageUrl = object.imageUrl;

        return this;
    }
}