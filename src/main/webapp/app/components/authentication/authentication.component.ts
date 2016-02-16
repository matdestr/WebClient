import {Component, OnInit} from 'angular2/core';
import {SignInComponent} from "./sign-in.component";
import {SignUpComponent} from "./sign-up.component";

@Component({
    selector: 'authentication',
    templateUrl: 'html/authentication.html',
    directives: [SignInComponent, SignUpComponent]
})
export class AuthenticationComponent implements OnInit{

    ngOnInit():any {
        return null;
    }
}