import {Component} from "angular2/core";
import {Input} from "angular2/core";
import {CardPosition} from "../../entities/session/card-position";
import {Circle} from "../../entities/session/circle";
import {OnChanges} from "angular2/core";

@Component({
    selector: 'game-circle',
    template: `
        <circle [attr.cx]="xCoordCenter" 
                [attr.cy]="yCoordCenter" 
                [attr.r]="circle.radius"
                stroke="red"
                stroke-width="30"
                fill="none"
                [class.svg-circle]="!isInnerCircle"
                [class.inner-circle]="isInnerCircle"></circle>
    `
})
export class CircleComponent implements OnChanges {
    @Input()
    public circle : Circle;
    
    @Input()
    public containerWidth : number = 700;
    
    @Input()
    public containerHeight : number = 700;
    
    @Input()
    public xCoordCenter : number = this.containerWidth / 2;
    
    @Input()
    public yCoordCenter : number = this.containerHeight / 2;
    
    @Input()
    public radius : number;
    
    @Input()
    public isInnerCircle : boolean;
    
    @Input()
    public cardPositions : CardPosition[];
    
    constructor() {
        this.cardPositions = [];
    }
    
    ngOnChanges(changes:{}):any {
        this.updateCardPositions();
    }

    public updateCardPositions() : void {
        var angle = 0;
        
        this.cardPositions.forEach(function(cardPosition : CardPosition) {
            var step = (2 * Math.PI) / this.cardPositions.length;
            
            var x = Math.round(this.containerWidth / 2 + this.radius * Math.cos(angle) - cardPosition.cardWidth / 2);
            var y = Math.round(this.containerHeight / 2 + this.radius * Math.sin(angle) - cardPosition.cardHeight / 2);
            
            angle += step;
        });
    }
}
