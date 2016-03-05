import {Serializable} from "../../util/serializable";
import {Topic} from "../../entities/topic/topic"

export class Category implements Serializable<Category>{
    public categoryId: number;
    public name : string;
    public description: string;
    public topics: Topic[];


    constructor(categoryId:number, name:string, description:string, topics:Topic[]) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.topics = topics;
    }

    public static createEmptyCategory():Category {
        return new Category(0, "","", []);
    }

    deserialize(object:Category):Category {
        this.name = object.name;
        this.description = object.description;
        this.categoryId = object.categoryId;

        var deserializedTopics:Topic[] = [];

        for (let topic of object.topics)
            deserializedTopics.push(Topic.createEmptyTopic().deserialize(topic));

        return this;
    }
}