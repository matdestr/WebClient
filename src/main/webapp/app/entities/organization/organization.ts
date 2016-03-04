import {User} from "../../entities/user/user";
import {Category} from "../../entities/category/category"

import {Email} from "../../entities/user/email";

export class Organization {
    public organizationId : number;
    public name : string;
    public owner : User;
    public members: User[];
    public categories: Category[];

    public static createEmptyOrganization() : Organization {
        var organization : Organization = new Organization();
        organization.organizationId = -1;
        organization.name = "";
        organization.owner = User.createEmptyUser();
        organization.members = new Array();
        organization.categories = new Array();

        return organization;
    }
}


export class CreateOrganization {
    public name:string;
    public emails: Email[];
}