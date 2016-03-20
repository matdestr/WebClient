import {Component, Input, Inject} from 'angular2/core';
import {Router} from "angular2/router";

/**
 * This component is responsible for all the functionality of sign out
 */
@Component({
    selector: 'sign-out',
    template: '<a [className]="anchorSignOutClassName" (click)="onSignOut()">Sign out</a>'
})
export class SignOutComponent {
    @Input() public anchorSignOutClassName;

    constructor(private _router : Router, @Inject('App.TokenName') private _tokenName : string) { }

    public onSignOut(){
        localStorage.removeItem(this._tokenName);
        localStorage.removeItem(this._tokenName + '-expire-date');
        localStorage.removeItem(this._tokenName + '-refresh-token');

        this._router.navigate(['Authentication'])
    }
}
