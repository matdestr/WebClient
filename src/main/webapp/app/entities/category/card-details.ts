import {Serializable} from "../../util/serializable";
import {User} from "../user/user";
import {Category} from "./category";
import {Topic} from "../topic/topic";

export class CardDetails implements Serializable<CardDetails> {

    public cardDetailsId:number;
    public text:string;
    public imageUrl:string;
    public creator:User;
    public category:Category;
    public topics:Topic[];
    // todo when comments needed, add comments array here!


    constructor(cardDetailsId:number, text:string, imageUrl:string, creator:User, category:Category, topics:Topic[]) {
        this.cardDetailsId = cardDetailsId;
        this.text = text;
        this.imageUrl = imageUrl;
        this.creator = creator;
        this.category = category;
        this.topics = topics;
    }

    public static createEmptyCard():CardDetails {
        return new CardDetails(0, "", "", User.createEmptyUser(), Category.createEmptyCategory(), []);
    }

    deserialize(object:CardDetails):CardDetails {
        this.cardDetailsId = object.cardDetailsId;
        this.text = object.text;
        this.imageUrl = object.imageUrl;
        this.creator = User.createEmptyUser().deserialize(object.creator);
        this.category = Category.createEmptyCategory().deserialize(object.category);

        var deserializedTopics:Topic[] = [];

        for (let topic of object.topics)
            deserializedTopics.push(Topic.createEmptyTopic().deserialize(topic));

        return this;
    }
}