import {Component} from 'angular2/core';

@Component({
    selector: 'register',
    template: `
        <form name="registration">
            <label for="username">username</label>
            <input id="username" type="text" class="span2" placeholder="kandoe1" required>
            <label for="name">name</label>
            <input id="name" type="text" class="span2" placeholder="John" required>
            <label for="surname">surname</label>
            <input id="surname" type="text" class="span2" placeholder="Doe" required>
            <label for="email">email</label>
            <input id="email" type="email" class="span2" placeholder="Doe" required>
            <label for="password">password</label>
            <input id="password" type="password" class="span2" placeholder="password" required>
            <label for="verify-password">verify password</label>
            <input id="verify-password" type="password" class="span2" placeholder="password" required>
            <input type="submit" value="Register" class="btn"/>
        </form>
    `
})
export class RegisterComponent {
}