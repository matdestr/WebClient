import {Serializable} from "../../util/serializable";

export class User implements Serializable<User> {
    public userId:number;
    public username:string;
    public name:string;
    public surname:string;
    public email:string;
    public profilePictureUrl:string;
    public roleTypes:Array<any>;


    constructor(userId: number, username:string, name:string, surname:string, email:string, profilePictureUrl:string = "profilepictures/default.png") {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public get fullName():string {
        return this.name + " " + this.surname;
    }

    public static createEmptyUser():User {
        return new User(0, "", "", "", "");
    }

    deserialize(object:User):User {
        this.userId = object.userId;
        this.username = object.username;
        this.name = object.name;
        this.surname = object.surname;
        this.email = object.email;
        this.profilePictureUrl = object.profilePictureUrl;
        this.roleTypes = object.roleTypes;

        return this;
    }
}