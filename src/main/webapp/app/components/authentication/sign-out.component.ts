import {Component, Input} from 'angular2/core';
import {Router} from "angular2/router";


@Component({
    selector: 'sign-out',
    template: '<a [className]="anchorSignOutClassName" (click)="onSignOut()">Sign out</a>'
})
export class SignOutComponent {
    @Input() public anchorSignOutClassName;

    constructor(private _router : Router) { }

    public onSignOut(){
        localStorage.removeItem('token');

        this._router.navigate(['Authentication'])
    }
}