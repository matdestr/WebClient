import {Component} from 'angular2/core';
import {SignOutComponent} from "../authentication/sign-out.component";

@Component({
    selector: 'toolbar',
    templateUrl: 'html/toolbar.html',
    directives:[SignOutComponent]
})
export class ToolbarComponent {
}