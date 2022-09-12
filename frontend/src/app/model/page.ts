import { Execution } from "./execution";

export interface Page<T> {
    content: T[],
    pageable: {
        sort: {
            sorted: boolean,
            unsorted: boolean,
            empty: boolean
        },
        pageNumber: number,
        pageSize: number,
        offset: number,
        paged: boolean,
        unpaged: boolean
    },
    totalPages: number,
    totalElements: number,
    last: boolean,
    first: boolean,
    sort: {
        sorted: boolean,
        unsorted: boolean,
        empty: boolean
    },
    size: number,
    number: number,
    numberOfElements: number,
    empty: boolean
}