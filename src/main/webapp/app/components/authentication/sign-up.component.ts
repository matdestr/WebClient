import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Response} from "angular2/http";
import {Router} from "angular2/router";

import {RegisterModel} from "../../entities/user/register";
import {Token} from "../../entities/authenticatie/token"
import {UserService} from "../../services/user.service"
import {User} from "../../entities/user/user";
import {TokenService} from "../../services/token.service";
import {ErrorDialogComponent} from "../widget/error-dialog.component";

@Component({
    selector: 'sign-up',
    templateUrl: 'html/sign-up.html',
    directives: [ErrorDialogComponent]
})
export class SignUpComponent {
    private errorMessages:string[] = [];
    private form: RegisterModel = new RegisterModel;

    constructor(private _userService: UserService, private _tokenService: TokenService, private _router: Router){

    }

    public onSubmit(){
        this.onError(null);
        this._userService.signUp(this.form).subscribe(
            (data) => this.handleData(data),
            (error) => {
                this.handleErrors(error)
            },
            () => this._router.navigate(['/Authentication'])
        );
    }

    public handleData(data: Response){
        if (data.status == 201){
            this._userService
                .signIn(this.form.username, this.form.password)
                .subscribe(
                    (token : Token) => {
                        this._tokenService.saveToken(token);
                    },
                    (error) => { console.log(error); },
                    () => { this._router.navigate(['/Dashboard']); });
            this.resetForm();
        }
    }

    public handleErrors(error: Response): void {
        var obj = JSON.parse(error.text());
        console.log(obj);
        if (obj.fieldErrors){
            if (obj.fieldErrors.length > 4)
                this.onError("Please fill in all fields.");
            else
                obj.fieldErrors.forEach(e => this.onError(e.message));
        } else {
            this.onError(obj.message);
        }

        this.resetForm();
    }

    public resetForm(): void {
        this.form.password = "";
        this.form.verifyPassword = "";
    }

    private onError(message:string){
        var self:any = this;

        if (message) {
            this.errorMessages.push(message);
        } else {
            this.errorMessages = [];
        }
    }
}