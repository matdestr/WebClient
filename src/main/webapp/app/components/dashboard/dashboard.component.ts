import {Component, OnInit} from 'angular2/core'
import {ToolbarComponent} from "../widget/toolbar.component";
import {getUsername, AuthConfig, tokenNotExpired} from "../../libraries/angular2-jwt";
import {CanActivate} from "angular2/router";

@Component({
    selector: 'dashboard',
    template: `
        <toolbar></toolbar>
    `,
    directives: [ToolbarComponent]
})
@CanActivate(() => tokenNotExpired('token')) // TODO : Inject
export class DashboardComponent implements OnInit{


    ngOnInit():any {
        var token = localStorage.getItem('token');
    }
}