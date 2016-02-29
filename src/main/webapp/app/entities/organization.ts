import {User} from "../entities/user/user";
export class Organization {
    public name : string;
    public owner : User;
    public members: User[];
}