import {CardPosition} from "./card-position";
export class Circle {
    public priority : number;
    public radius : number;
    public cardPositions : CardPosition[];
    
    constructor (priority : number) {
        this.priority = priority;
        this.cardPositions = [];
    }
}
