package httpServer;

public enum Endpoint {
    PUT_TASK, PUT_SUBTASK, PUT_EPIC,
    GET_TASK, GET_SUBTASK, GET_EPIC, GET_TASKS, GET_SUBTASKS, GET_EPICS,
    GET_SUBTASKS_FROM_EPIC, GET_PRIORITIZED_TASKS, GET_HISTORY,
    DELETE_TASK, DELETE_SUBTASK, DELETE_EPIC, DELETE_TASKS, DELETE_SUBTASKS, DELETE_EPICS,
    UNKNOWN
}
