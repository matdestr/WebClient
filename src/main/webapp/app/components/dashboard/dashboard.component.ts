import {Component, OnInit} from 'angular2/core'
import {ToolbarComponent} from "../widget/toolbar.component";
import {getUsername, AuthConfig} from "../../libraries/angular2-jwt";

@Component({
    selector: 'dashboard',
    template: `
        <toolbar></toolbar>
    `,
    directives: [ToolbarComponent]
})
export class DashboardComponent implements OnInit{


    ngOnInit():any {
        var token = localStorage.getItem('token');

        alert(getUsername(token));
    }
}