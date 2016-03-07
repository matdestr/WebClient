import {Component, OnInit, Input} from "angular2/core";
import {Router} from "angular2/router";
import {RouteParams} from "angular2/router";
import {ToolbarComponent} from "../widget/toolbar.component";

@Component({
    selector: 'active-session',
    templateUrl: 'html/active-session.html',
    directives: [ToolbarComponent]
})
export class ActiveSessionComponent {
    constructor(private _router:Router,
                private _routeArgs:RouteParams) {

    }
}