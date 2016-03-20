/**
 * This class is used to create a new category.
 * */
export class CreateCategoryModel {
    name:string = "";
    description:string = "";
    listTagId: number[] = [];


    constructor(name:string, description:string, listTagId:number[]) {
        this.name = name;
        this.description = description;
        this.listTagId = listTagId;
    }

    public static createEmptyCreateCategory():CreateCategoryModel {
        return new CreateCategoryModel("", "", []);
    }
}
