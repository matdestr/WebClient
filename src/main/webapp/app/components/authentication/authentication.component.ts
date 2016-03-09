import {Component, OnInit, Inject} from 'angular2/core';
import {SignInComponent} from "./sign-in.component";
import {SignUpComponent} from "./sign-up.component";
import {tokenNotExpired} from "../../libraries/angular2-jwt";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";

@Component({
    selector: 'authentication',
    templateUrl: 'html/authentication.html',
    directives: [SignInComponent, SignUpComponent]
})
export class AuthenticationComponent implements OnInit{
    constructor(@Inject('App.TokenName') tokenName : string, router : Router) {
        if (tokenNotExpired(tokenName)) {
            router.navigate(['/Dashboard']);
        } else {
            console.log("Token is expired...");
        }
    }
    
    ngOnInit():any {
        return null;
    }
}
