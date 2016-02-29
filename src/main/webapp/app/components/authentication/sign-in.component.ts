import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Router} from "angular2/router";

import {UserService} from "../../services/user.service";
import {TokenService} from "../../services/token.service";
import {Token} from "../../entities/authenticatie/token";

@Component({
    selector: 'sign-in',
    templateUrl: 'html/sign-in.html',
    directives: [NgForm]
})
export class SignInComponent{
    private username : string;
    private password : string;
    private errors: Array<String> = new Array();

    constructor(private _signInService : UserService, private _tokenService: TokenService, private _router : Router) { }

    private onSubmit() {
        if (!this.username || !this.password) {
            this.errors.push('username and password are required');
        }

        this._signInService
            .signIn(this.username, this.password)
            .subscribe(
                (token : Token) => {
                    this._tokenService.saveToken(token)
                },
                error => {
                    if (error.status == 400){
                        this.errors.push('Username and/or password are wrong')
                    }

                },
                () => { this._router.navigate(['/Dashboard']); });
    }
}
