import {Component} from 'angular2/core';

@Component({
    selector: 'sign-in',
    template: `
        <form name="sign-in">
            <label for="username">username</label>
            <input id="username" type="text" class="span2" placeholder="username">
            <label for="password">password</label>
            <input id="password" type="password" class="span2" placeholder="password">
            <input type="submit" value="Sign in" class="btn"/>
        </form>
    `
})
export class SignInComponent {
}