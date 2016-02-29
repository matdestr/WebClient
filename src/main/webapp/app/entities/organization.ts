import {User} from "../entities/user/user";
import {Category} from "../entities/category"

export class Organization {
    public name : string;
    public owner : User;
    public members: User[];
    public categories: Category[];
}