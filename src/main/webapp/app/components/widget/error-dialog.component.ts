import {Component} from "angular2/core";

@Component({
    selector: 'error-dialog',
    template: `
        <p class="alert alert-danger">{{message}}</p>
    `
})
export class ErrorDialogComponent {
    public message : string;
    
    constructor() {
        this.message = 'Could not connect to the server. Please check your internet connection or try again later.';
    }
}
