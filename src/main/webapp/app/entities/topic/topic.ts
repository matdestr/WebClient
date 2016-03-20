import {Serializable} from "../../util/serializable";

/**
 * Entity that represents a topic JSON object
 */

export class Topic implements Serializable<Topic>{
    public topicId: number;
    public name : string;
    public description: string;
    public categoryId: number;

    constructor(topicId: number, name:string, description:string, categoryId: number) {
        this.topicId = topicId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
    }

    public static createEmptyTopic(): Topic{
        return new Topic(0, "", "", 0);
    }

    deserialize(object:Topic):Topic {
        this.topicId = object.topicId;
        this.name = object.name;
        this.description = object.description;
        this.categoryId = object.categoryId;

        return this;
    }
}