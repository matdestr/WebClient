import {Component} from 'angular2/core'

@Component({
    selector: 'spinner',
    template: `
         <div class="spinner">
            <div class="spinner-item spinner-item1"></div>
            <div class="spinner-item spinner-item2"></div>
            <div class="spinner-item spinner-item3"></div>
        </div>
    `
})
export class SpinnerComponent {
}