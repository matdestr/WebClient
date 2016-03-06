export class CreateCategoryModel {
    name:string = "";
    description:string = "";


    constructor(name:string, description:string) {
        this.name = name;
        this.description = description;
    }

    public static createEmptyCreateCategory():CreateCategoryModel {
        return new CreateCategoryModel("", "");
    }
}