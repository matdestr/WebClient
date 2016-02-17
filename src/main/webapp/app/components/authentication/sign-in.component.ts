import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Router} from "angular2/router";

import {SignInService} from "../../services/sign-in.service";
import {Token} from "../../entities/authenticatie/token";

@Component({
    selector: 'sign-in',
    templateUrl: 'html/sign-in.html',
    directives: [NgForm]
})
export class SignInComponent {
    private username : string;
    private password : string;
    private invalidCredentials : boolean = false;
    
    constructor(private signInService : SignInService, private router : Router) { }
    
    private onSubmit() {
        if (!this.username || !this.password) {
            // TODO : Display error
            console.log('username and password are required');
        }
        
        this.signInService
            .signIn(this.username, this.password)
            // TODO : Fancy error
            .subscribe(
                (token : Token) => {
                    localStorage.setItem('token', JSON.stringify(token.access_token))
                    console.log(token); // TODO : Remove debug info
                },
                error => {console.error(error); this.invalidCredentials = true},
                () => { this.router.navigate(['/Dashboard']); });
    }
}
