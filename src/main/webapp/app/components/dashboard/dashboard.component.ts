import {Component, OnInit} from 'angular2/core'
import {ToolbarComponent} from "../widget/toolbar.component";
import {getUsername, AuthConfig, tokenNotExpired} from "../../libraries/angular2-jwt";
import {CanActivate} from "angular2/router";
import {isTokenExpired} from "../../services/token.service";

@Component({
    selector: 'dashboard',
    template: `
        <toolbar></toolbar>
    `,
    directives: [ToolbarComponent]
})
//@CanActivate(() => isTokenExpired())
export class DashboardComponent implements OnInit{

    public constructor(){
    }

    ngOnInit():any {
        var token = localStorage.getItem('token');
    }
}