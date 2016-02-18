export interface Serializable<T> {
        deserialize(object: T): T;
}