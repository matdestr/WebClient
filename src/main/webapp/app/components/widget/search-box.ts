import {Component, Output, EventEmitter} from 'angular2/core'

/**
 * This component offers functionality for implementing a search bar.
 * */
@Component({
    selector: 'search-box',
    template: `
        <div id="custom-search-input">
            <div class="input-group col-md-12">
                <input #input (input)="update.emit(input.value)" type="text" class="form-control input-lg" placeholder="Search"/>
                    <span class="input-group-addon">
                            <i class="glyphicon glyphicon-search"></i>
                    </span>
            </div>
        </div>`
})
export class SearchBoxComponent {
    @Output() public update: EventEmitter<string> = new EventEmitter();
}