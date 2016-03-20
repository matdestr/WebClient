/**
 * Enum for the SessionStatus
 */
export enum SessionStatus {
    CREATED = <any> 'CREATED',
    USERS_JOINING = <any> 'USERS_JOINING',
    ADDING_CARDS = <any> 'ADDING_CARDS',
    REVIEWING_CARDS = <any> 'REVIEWING_CARDS',
    CHOOSING_CARDS = <any> 'CHOOSING_CARDS',
    READY_TO_START = <any> 'READY_TO_START',
    IN_PROGRESS = <any> 'IN_PROGRESS',
    FINISHED = <any> 'FINISHED'
}
