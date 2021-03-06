import {Component, Input} from "angular2/core";
import {NgFor, NgIf} from "angular2/common";

/**
 * This component is responsible for showing a dialog when an error occurs.
 * */
@Component({
    selector: 'error-dialog',
    template: `
    <div *ngIf="messages.length > 0" class="alert alert-danger">
        <ul class="error-list" *ngFor="#message of messages; #i = index">
            <li><span class="error-icon glyphicon glyphicon-remove-circle"></span>{{ messages[i] }}</li>
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
