import {Component} from 'angular2/core';
import {NgForm} from "angular2/common";
import {Response} from "angular2/http";
import {Router} from "angular2/router";
import {RegisterModel} from "../../entities/register/register";
import {SignUpService} from "../../services/sing-up.service";

@Component({
    selector: 'sign-up',
    templateUrl: 'html/sign-up.html'
})
export class SignUpComponent {
    private form: RegisterModel = new RegisterModel;
    private errors: Array<String> = new Array();

    constructor(private _signUpService: SignUpService){

    }

    public onSubmit(){
        this._signUpService.signUp(this.form).subscribe(
            data => console.log(data),
            error => this.handleErrors(error),
            () => console.log("complete")
        );
    }

    public handleErrors(error: Response): void {
        this.errors = new Array();
        if(error.status == 422){
            let json = error.json();
            json.fieldErrors.forEach(e => this.errors.push(e.message));
        } else {
            //todo better handling
            this.errors.push(error.text());
        }
    }
}