export enum SessionStatus {
    /*CREATED = <any> "CREATED",
    USERS_JOINING = <any> "USERS_JOINING",
    ADDING_CARDS = <any> "ADDING_CARDS",
    REVIEWING_CARDS = <any> "REVIEWING_CARDS",
    CHOOSING_CARDS = <any> "CHOOSING_CARDS",
    READY_TO_START = <any> "READY_TO_START",
    IN_PROGRESS = <any> "IN_PROGRESS",
    FINISHED = <any> "FINISHED"*/

    CREATED = <any> 'CREATED',
    USERS_JOINING = <any> 'USERS_JOINING',
    ADDING_CARDS = <any> 'ADDING_CARDS',
    REVIEWING_CARDS = <any> 'REVIEWING_CARDS',
    CHOOSING_CARDS = <any> 'CHOOSING_CARDS',
    READY_TO_START = <any> 'READY_TO_START',
    IN_PROGRESS = <any> 'IN_PROGRESS',
    FINISHED = <any> 'FINISHED'
}

/**
 * Workaround: handles the mapping between a received string value and the enum value
 * */
/*export function sessionStatusFromString(sessionStatus : string) : SessionStatus {
    switch (sessionStatus.toString().toUpperCase()) {
        case 'CREATED':
            return SessionStatus.CREATED;
        case 'USERS_JOINING':
            return SessionStatus.USERS_JOINING;
        case 'ADDING_CARDS':
            return SessionStatus.ADDING_CARDS;
        case "REVIEWING_CARDS":
            return SessionStatus.REVIEWING_CARDS;
        case "CHOOSING_CARDS":
            return SessionStatus.CHOOSING_CARDS;
        case "READY_TO_START":
            return SessionStatus.READY_TO_START;
        case "IN_PROGRESS":
            return SessionStatus.IN_PROGRESS;
        case "FINISHED":
            return SessionStatus.FINISHED;
    }
    
    console.log('Tried to parse invalid session status: ' + sessionStatus);
}*/
