import {Serializable} from "../../util/serializable";

export class Topic implements Serializable<Topic>{
    public name : string;
    public description: string;

    constructor(name:string, description:string) {
        this.name = name;
        this.description = description;
    }

    public static createEmptyTopic(): Topic{
        return new Topic("", "");
    }

    deserialize(object:Topic):Topic {
        this.name = object.name;
        this.description = object.description;

        return this;
    }
}