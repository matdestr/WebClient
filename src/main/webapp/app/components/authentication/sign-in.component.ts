import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Router} from "angular2/router";

import {UserService} from "../../services/user.service";
import {TokenService} from "../../services/token.service";
import {Token} from "../../entities/authenticatie/token";
import {ErrorDialogComponent} from "../widget/error-dialog.component";
import {ViewEncapsulation} from "angular2/core";

@Component({
    selector: 'sign-in',
    templateUrl: 'html/sign-in.html',
    directives: [NgForm, ErrorDialogComponent],
    styleUrls: [ 'css/home.css' ],
    encapsulation: ViewEncapsulation.None
})
export class SignInComponent{
    private username : string;
    private password : string;
    private errorMessages:string[] = [];

    constructor(private _signInService : UserService, private _tokenService: TokenService, private _router : Router) {
    }

    private onSubmit() {
        this.onError(null);

        if (!this.username || !this.password) {
            this.onError(this.messageToJson("Username and password are required."));
        } else {

            this._signInService
                .signIn(this.username, this.password)
                .subscribe(
                    (token:Token) => {
                        this._tokenService.saveToken(token)
                    },
                    error => {
                        if (error.status == 400) {
                            this.onError(this.messageToJson("Invalid username or password."));
                        }

                    },
                    () => {
                        this._router.navigate(['/Dashboard']);
                    });
        }
    }

    private onError(message) : void {
        var self:any = this;
        if (message) {
            var obj = JSON.parse(message);
            if (obj) {
                this.errorMessages.push(obj.message);
                setTimeout(() => {
                    self.errorMessages.splice(0);
                }, 2000);
            }
        } else
            this.errorMessages = [];
    }

    private messageToJson(message) : string {
        if (typeof(message) !== "string")
            return null;

        return "{\"message\":\"" + message +"\"}";
    }
}
