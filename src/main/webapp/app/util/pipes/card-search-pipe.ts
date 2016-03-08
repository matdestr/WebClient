import {Pipe, PipeTransform} from 'angular2/core'
import {CardDetails} from "../../entities/category/card-details";

@Pipe({
    name: "cardSearch"
})
export class CardSearchPipe implements PipeTransform {

    transform(value:CardDetails[], args:string[]):any {
        var term:string = args[0];
        term = term.trim();

        if (term == "")
            return value;
        else
            return value.filter((card) => card.text.toLowerCase().indexOf(term.toLocaleLowerCase()) >= 0 || card.active);

    }
}