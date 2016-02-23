import {Serializable} from "../../util/serializable";

export class User implements Serializable<User> {
    public userId:number;
    public username:string;
    public name:string;
    public surname:string;
    public email:string;


    constructor(username:string, name:string, surname:string, email:string) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public get fullName():string {
        return this.name + " " + this.surname;
    }

    public static createEmptyUser():User {
        return new User("", "", "", "");
    }

    deserialize(object:User):User {
        this.userId = object.userId;
        this.username = object.username;
        this.name = object.name;
        this.surname = object.surname;
        this.email = object.email;

        return this;
    }
}