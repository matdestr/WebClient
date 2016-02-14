import {Component} from 'angular2/core';
import {RegisterComponent} from "./authentication/register.component";
import {ToolbarComponent} from "./toolbar.component";

@Component({
    selector: 'my-app',
    template: `
        <header>
            <toolbar></toolbar>
        </header>

        <div id="main-content">
        <section id="register">
            <register></register>
        </section>
        </div>

        <footer></footer>
    `,
    directives: [ToolbarComponent, RegisterComponent]
})
export class AppComponent {
}