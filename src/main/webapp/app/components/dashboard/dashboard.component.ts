import {Component} from 'angular2/core'
import {ToolbarComponent} from "../toolbar.component";

@Component({
    selector: 'dashboard',
    template: `
        <toolbar></toolbar>
    `,
    directives: [ToolbarComponent]
})
export class DashboardComponent {
}