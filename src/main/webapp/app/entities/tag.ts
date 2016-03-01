import {Serializable} from "../util/serializable";

export class Tag implements Serializable<Tag>{
    public name : string;

    constructor(name:string) {
        this.name = name;
    }

    deserialize(object:Tag):Tag {
        this.name = object.name;

        return this;
    }

     static createEmptyTag():Tag{
        return new Tag("");
    }
}