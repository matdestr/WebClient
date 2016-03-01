import {User} from "../entities/user/user";
import {Category} from "../entities/category/category"

import {Email} from "../entities/user/email";

export class Organization {
    public organizationId : number;
    public name : string;
    public owner : User;
    public members: User[];
    public categories: Category[];
}

export class CreateOrganization {
    public name:string;
    public emails: Email[];
}