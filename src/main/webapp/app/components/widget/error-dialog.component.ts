import {Component} from "angular2/core";

@Component({
    selector: 'error-dialog',
    template: `
        <div class="error-dialog">
            <p>{{message}}</p>
        </div>
    `
})
export class ErrorDialogComponent {
    public message : string;
    
    constructor() {
        this.message = 'Could not connect to the server. Please check your internet connection or try again later.';
    }
}