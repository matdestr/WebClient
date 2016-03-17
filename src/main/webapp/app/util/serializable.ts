/**
 * An interface for entities which can be deserialized from JSON.
 * */
export interface Serializable<T> {
    /**
     * Manually deserializes a JSON structure to the actual entity to preserve any possible function implementations.
     * 
     * @param object The JSON object to parse
     * */
    deserialize(object : T) : T;
}
