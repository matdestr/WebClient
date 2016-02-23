import {Component} from "angular2/core";
import {NgForm} from "angular2/common";

import {UpdateUserModel} from "../../entities/user/edit";
import {UserService} from "../../services/user.service";


@Component({
    selector: 'edit-profile',
    templateUrl: 'html/editprofile.html'
})

export class UserProfileEditComponent{
    private form:UpdateUserModel = new UpdateUserModel();

    constructor(private _userService: UserService){

    }

    public onSubmit(){
        this._userService.updateUser(this.form)
            .subscribe(
                data => console.log(data),
                error => console.log(error),
                () => console.log("user put")
        );
    }
}