import {Component} from 'angular2/core'
import {AuthenticationComponent} from "./authentication.component";

@Component({
    selector: 'welcome',
    template: `
        <div id="main-content" >
        <section id="authentication">
            <authentication></authentication>
        </section>
        </div>
    `,
    directives:[AuthenticationComponent]
})
export class WelcomeComponent {
}