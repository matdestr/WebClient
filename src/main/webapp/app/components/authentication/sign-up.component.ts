import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Response} from "angular2/http";
import {Router} from "angular2/router";

import {RegisterModel} from "../../entities/user/register";
import {Token} from "../../entities/authenticatie/token"
import {UserService} from "../../services/user.service"
import {User} from "../../entities/user/user";
import {TokenService} from "../../services/token.service";

@Component({
    selector: 'sign-up',
    templateUrl: 'html/sign-up.html'
})
export class SignUpComponent {
    private form: RegisterModel = new RegisterModel;
    private errors: Array<String> = new Array();

    constructor(private _userService: UserService, private _tokenService: TokenService, private _router: Router){

    }

    public onSubmit(){
        this._userService.signUp(this.form).subscribe(
            data => this.handleData(data),
            error => this.handleErrors(error),
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
                    error => { console.log(error); },
                    () => { this._router.navigate(['/Dashboard']); });
            this.resetForm();
        }
    }

    public handleErrors(error: Response): void {
        this.resetForm();
        if(error.status == 422){
            let json = error.json();
            json.fieldErrors.forEach(e => this.errors.push(e.message));
        } else {
            console.log(error);
            this.errors.push("Oops. Something went wrong!");
        }
    }

    public resetForm(): void {
        this.errors = new Array();
        this.form.password = "";
        this.form.verifyPassword = "";
    }
}