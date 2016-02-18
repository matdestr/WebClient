import {Serializable} from "../util/serializable";

export class User implements Serializable<User> {
    public name:string;
    public surname:string;
    public email:string;


    constructor(name:string, surname:string, email:string) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public get fullName():string {
        return this.name + " " + this.surname;
    }

    public static createEmptyUser():User {
        return new User("", "", "");
    }

    deserialize(object:User):User {
        this.name = object.name;
        this.surname = object.surname;
        this.email = object.email;

        return this;
    }
}