import {Component, Input} from "angular2/core";
import {NgFor} from "angular2/common";

@Component({
    selector: 'error-dialog',
    template: `
    <div class="alert alert-danger">
        <ul class="error-list" *ngFor="#message of messages; #i = index">
            <li>{{ messages[i] }}</li>
        </ul>
    </div>
    `
})
export class ErrorDialogComponent {
    @Input() public messages : string[];
    
    constructor() {
        this.messages = new Array();
    }
}
