import {Component} from 'angular2/core'
import {AuthenticationComponent} from "./authentication.component";

@Component({
    selector: 'welcome',
    template: `
        <div id="main-content" >
        <section id="authentication" class="flex-container">
            <authentication class="flex-item"></authentication>
        </section>
        </div>

        <footer>
            <p>test <a>link</a></p>
        </footer>

    `,
    directives:[AuthenticationComponent]
})
export class WelcomeComponent {
}